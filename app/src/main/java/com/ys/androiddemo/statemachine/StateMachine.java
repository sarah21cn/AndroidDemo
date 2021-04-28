package com.ys.androiddemo.statemachine;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

/**
 * 状态机，copy from com.android.internal.util.StateMachine
 */
public class StateMachine {
  private static final String TAG = "StateMachine";
  private String mName;
  /** Message.what value when quitting */
  private static final int SM_QUIT_CMD = -1;
  /** Message.what value when initializing */
  private static final int SM_INIT_CMD = -2;
  /**
   * Convenience constant that maybe returned by processMessage
   * to indicate the the message was processed and is not to be
   * processed by parent states
   */
  public static final boolean HANDLED = true;
  /**
   * Convenience constant that maybe returned by processMessage
   * to indicate the the message was NOT processed and is to be
   * processed by parent states
   */
  public static final boolean NOT_HANDLED = false;
  /**
   * StateMachine logging record.
   * {@hide}
   */
  public static class LogRec {
    private long mTime;
    private int mWhat;
    private String mInfo;
    private State mState;
    private State mOrgState;
    /**
     * Constructor
     *
     * @param msg
     * @param state that handled the message
     * @param orgState is the first state the received the message but
     * did not processes the message.
     */
    LogRec(Message msg, String info, State state, State orgState) {
      update(msg, info, state, orgState);
    }
    /**
     * Update the information in the record.
     * @param state that handled the message
     * @param orgState is the first state the received the message but
     * did not processes the message.
     */
    public void update(Message msg, String info, State state, State orgState) {
      mTime = System.currentTimeMillis();
      mWhat = (msg != null) ? msg.what : 0;
      mInfo = info;
      mState = state;
      mOrgState = orgState;
    }
    /**
     * @return time stamp
     */
    public long getTime() {
      return mTime;
    }
    /**
     * @return msg.what
     */
    public long getWhat() {
      return mWhat;
    }
    /**
     * @return the command that was executing
     */
    public String getInfo() {
      return mInfo;
    }
    /**
     * @return the state that handled this message
     */
    public State getState() {
      return mState;
    }
    /**
     * @return the original state that received the message.
     */
    public State getOriginalState() {
      return mOrgState;
    }
    /**
     * @return as string
     */
    public String toString(StateMachine sm) {
      StringBuilder sb = new StringBuilder();
      sb.append("time=");
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(mTime);
      sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", c, c, c, c, c, c));
      sb.append(" state=");
      sb.append(mState == null ? "<null>" : mState.getName());
      sb.append(" orgState=");
      sb.append(mOrgState == null ? "<null>" : mOrgState.getName());
      sb.append(" what=");
      String what = sm.getWhatToString(mWhat);
      if (TextUtils.isEmpty(what)) {
        sb.append(mWhat);
        sb.append("(0x");
        sb.append(Integer.toHexString(mWhat));
        sb.append(")");
      } else {
        sb.append(what);
      }
      if ( ! TextUtils.isEmpty(mInfo)) {
        sb.append(" ");
        sb.append(mInfo);
      }
      return sb.toString();
    }
  }
  /**
   * A list of log records including messages recently processed by the state machine.
   *
   * The class maintains a list of log records including messages
   * recently processed. The list is finite and may be set in the
   * constructor or by calling setSize. The public interface also
   * includes size which returns the number of recent records,
   * count which is the number of records processed since the
   * the last setSize, get which returns a record and
   * add which adds a record.
   */
  private static class LogRecords {
    private static final int DEFAULT_SIZE = 100;
    private Vector<LogRec> mLogRecords = new Vector<LogRec>();
    private int mMaxSize = DEFAULT_SIZE;
    private int mOldestIndex = 0;
    private int mCount = 0;
    /**
     * private constructor use add
     */
    private LogRecords() {
    }
    /**
     * Set size of messages to maintain and clears all current records.
     *
     * @param maxSize number of records to maintain at anyone time.
     */
    synchronized void setSize(int maxSize) {
      mMaxSize = maxSize;
      mCount = 0;
      mLogRecords.clear();
    }
    /**
     * @return the number of recent records.
     */
    synchronized int size() {
      return mLogRecords.size();
    }
    /**
     * @return the total number of records processed since size was set.
     */
    synchronized int count() {
      return mCount;
    }
    /**
     * Clear the list of records.
     */
    synchronized void cleanup() {
      mLogRecords.clear();
    }
    /**
     * @return the information on a particular record. 0 is the oldest
     * record and size()-1 is the newest record. If the index is to
     * large null is returned.
     */
    synchronized LogRec get(int index) {
      int nextIndex = mOldestIndex + index;
      if (nextIndex >= mMaxSize) {
        nextIndex -= mMaxSize;
      }
      if (nextIndex >= size()) {
        return null;
      } else {
        return mLogRecords.get(nextIndex);
      }
    }
    /**
     * Add a processed message.
     *
     * @param msg
     * @param messageInfo to be stored
     * @param state that handled the message
     * @param orgState is the first state the received the message but
     * did not processes the message.
     */
    synchronized void add(Message msg, String messageInfo, State state, State orgState) {
      mCount += 1;
      if (mLogRecords.size() < mMaxSize) {
        mLogRecords.add(new LogRec(msg, messageInfo, state, orgState));
      } else {
        LogRec pmi = mLogRecords.get(mOldestIndex);
        mOldestIndex += 1;
        if (mOldestIndex >= mMaxSize) {
          mOldestIndex = 0;
        }
        pmi.update(msg, messageInfo, state, orgState);
      }
    }
  }
  private static class SmHandler extends Handler {
    /** The debug flag */
    private boolean mDbg = true;
    /** The SmHandler object, identifies that message is internal */
    private static final Object mSmHandlerObj = new Object();
    /** The current message */
    private Message mMsg;
    /** A list of log records including messages this state machine has processed */
    private LogRecords mLogRecords = new LogRecords();
    /** true if construction of the state machine has not been completed */
    private boolean mIsConstructionCompleted;
    /** Stack used to manage the current hierarchy of states */
    private StateInfo mStateStack[];
    /** Top of mStateStack */
    private int mStateStackTopIndex = -1;
    /** A temporary stack used to manage the state stack */
    private StateInfo mTempStateStack[];
    /** The top of the mTempStateStack */
    private int mTempStateStackCount;
    /** State used when state machine is halted */
    private HaltingState mHaltingState = new HaltingState();
    /** State used when state machine is quitting */
    private QuittingState mQuittingState = new QuittingState();
    /** Reference to the StateMachine */
    private StateMachine mSm;
    /**
     * Information about a state.
     * Used to maintain the hierarchy.
     */
    private class StateInfo {
      /** The state */
      State state;
      /** The parent of this state, null if there is no parent */
      StateInfo parentStateInfo;
      /** True when the state has been entered and on the stack */
      boolean active;
      /**
       * Convert StateInfo to string
       */
      @Override
      public String toString() {
        return "state=" + state.getName() + ",active=" + active
            + ",parent=" + ((parentStateInfo == null) ?
            "null" : parentStateInfo.state.getName());
      }
    }
    /** The map of all of the states in the state machine */
    private HashMap<State, StateInfo> mStateInfo =
        new HashMap<State, StateInfo>();
    /** The initial state that will process the first message */
    private State mInitialState;
    /** The destination state when transitionTo has been invoked */
    private State mDestState;
    /** The list of deferred messages */
    private ArrayList<Message> mDeferredMessages = new ArrayList<Message>();
    /**
     * State entered when transitionToHaltingState is called.
     */
    private class HaltingState extends State {
      @Override
      public boolean processMessage(Message msg) {
        mSm.haltedProcessMessage(msg);
        return true;
      }
    }
    /**
     * State entered when a valid quit message is handled.
     */
    private class QuittingState extends State {
      @Override
      public boolean processMessage(Message msg) {
        return NOT_HANDLED;
      }
    }
    /**
     * Handle messages sent to the state machine by calling
     * the current state's processMessage. It also handles
     * the enter/exit calls and placing any deferred messages
     * back onto the queue when transitioning to a new state.
     */
    @Override
    public final void handleMessage(Message msg) {
      if (mDbg) Log.d(TAG, "handleMessage: E msg.what=" + msg.what);
      /** Save the current message */
      mMsg = msg;
      if (mIsConstructionCompleted) {
        /** Normal path */
        processMsg(msg);
      } else if (!mIsConstructionCompleted &&
          (mMsg.what == SM_INIT_CMD) && (mMsg.obj == mSmHandlerObj)) {
        /** Initial one time path. */
        mIsConstructionCompleted = true;
        invokeEnterMethods(0);
      } else {
        throw new RuntimeException("StateMachine.handleMessage: " +
            "The start method not called, received msg: " + msg);
      }
      performTransitions();
      if (mDbg) Log.d(TAG, "handleMessage: X");
    }
    /**
     * Do any transitions
     */
    private void performTransitions() {
      /**
       * If transitionTo has been called, exit and then enter
       * the appropriate states. We loop on this to allow
       * enter and exit methods to use transitionTo.
       */
      State destState = null;
      while (mDestState != null) {
        if (mDbg) Log.d(TAG, "handleMessage: new destination call exit");
        /**
         * Save mDestState locally and set to null
         * to know if enter/exit use transitionTo.
         */
        destState = mDestState;
        mDestState = null;
        /**
         * Determine the states to exit and enter and return the
         * common ancestor state of the enter/exit states. Then
         * invoke the exit methods then the enter methods.
         */
        StateInfo commonStateInfo = setupTempStateStackWithStatesToEnter(destState);
        invokeExitMethods(commonStateInfo);
        int stateStackEnteringIndex = moveTempStateStackToStateStack();
        invokeEnterMethods(stateStackEnteringIndex);
        /**
         * Since we have transitioned to a new state we need to have
         * any deferred messages moved to the front of the message queue
         * so they will be processed before any other messages in the
         * message queue.
         */
        moveDeferredMessageAtFrontOfQueue();
      }
      /**
       * After processing all transitions check and
       * see if the last transition was to quit or halt.
       */
      if (destState != null) {
        if (destState == mQuittingState) {
          /**
           * Call onQuitting to let subclasses cleanup.
           */
          mSm.onQuitting();
          cleanupAfterQuitting();
        } else if (destState == mHaltingState) {
          /**
           * Call onHalting() if we've transitioned to the halting
           * state. All subsequent messages will be processed in
           * in the halting state which invokes haltedProcessMessage(msg);
           */
          mSm.onHalting();
        }
      }
    }
    /**
     * Cleanup all the static variables and the looper after the SM has been quit.
     */
    private final void cleanupAfterQuitting() {
      if (mSm.mSmThread != null) {
        // If we made the thread then quit looper which stops the thread.
        getLooper().quit();
        mSm.mSmThread = null;
      }
      mSm.mSmHandler = null;
      mSm = null;
      mMsg = null;
      mLogRecords.cleanup();
      mStateStack = null;
      mTempStateStack = null;
      mStateInfo.clear();
      mInitialState = null;
      mDestState = null;
      mDeferredMessages.clear();
    }
    /**
     * Complete the construction of the state machine.
     */
    private final void completeConstruction() {
      if (mDbg) Log.d(TAG, "completeConstruction: E");
      /**
       * Determine the maximum depth of the state hierarchy
       * so we can allocate the state stacks.
       */
      int maxDepth = 0;
      for (StateInfo si : mStateInfo.values()) {
        int depth = 0;
        for (StateInfo i = si; i != null; depth++) {
          i = i.parentStateInfo;
        }
        if (maxDepth < depth) {
          maxDepth = depth;
        }
      }
      if (mDbg) Log.d(TAG, "completeConstruction: maxDepth=" + maxDepth);
      mStateStack = new StateInfo[maxDepth];
      mTempStateStack = new StateInfo[maxDepth];
      setupInitialStateStack();
      /** Sending SM_INIT_CMD message to invoke enter methods asynchronously */
      sendMessageAtFrontOfQueue(obtainMessage(SM_INIT_CMD, mSmHandlerObj));
      if (mDbg) Log.d(TAG, "completeConstruction: X");
    }
    /**
     * Process the message. If the current state doesn't handle
     * it, call the states parent and so on. If it is never handled then
     * call the state machines unhandledMessage method.
     */
    private final void processMsg(Message msg) {
      StateInfo curStateInfo = mStateStack[mStateStackTopIndex];
      if (mDbg) {
        Log.d(TAG, "processMsg: " + curStateInfo.state.getName());
      }
      if (isQuit(msg)) {
        transitionTo(mQuittingState);
      } else {
        while (!curStateInfo.state.processMessage(msg)) {
          /**
           * Not processed
           */
          curStateInfo = curStateInfo.parentStateInfo;
          if (curStateInfo == null) {
            /**
             * No parents left so it's not handled
             */
            mSm.unhandledMessage(msg);
            break;
          }
          if (mDbg) {
            Log.d(TAG, "processMsg: " + curStateInfo.state.getName());
          }
        }
        /**
         * Record that we processed the message
         */
        if (mSm.recordLogRec(msg)) {
          if (curStateInfo != null) {
            State orgState = mStateStack[mStateStackTopIndex].state;
            mLogRecords.add(msg, mSm.getLogRecString(msg), curStateInfo.state,
                orgState);
          } else {
            mLogRecords.add(msg, mSm.getLogRecString(msg), null, null);
          }
        }
      }
    }
    /**
     * Call the exit method for each state from the top of stack
     * up to the common ancestor state.
     */
    private final void invokeExitMethods(StateInfo commonStateInfo) {
      while ((mStateStackTopIndex >= 0) &&
          (mStateStack[mStateStackTopIndex] != commonStateInfo)) {
        State curState = mStateStack[mStateStackTopIndex].state;
        if (mDbg) Log.d(TAG, "invokeExitMethods: " + curState.getName());
        curState.exit();
        mStateStack[mStateStackTopIndex].active = false;
        mStateStackTopIndex -= 1;
      }
    }
    /**
     * Invoke the enter method starting at the entering index to top of state stack
     */
    private final void invokeEnterMethods(int stateStackEnteringIndex) {
      for (int i = stateStackEnteringIndex; i <= mStateStackTopIndex; i++) {
        if (mDbg) Log.d(TAG, "invokeEnterMethods: " + mStateStack[i].state.getName());
        mStateStack[i].state.enter();
        mStateStack[i].active = true;
      }
    }
    /**
     * Move the deferred message to the front of the message queue.
     */
    private final void moveDeferredMessageAtFrontOfQueue() {
      /**
       * The oldest messages on the deferred list must be at
       * the front of the queue so start at the back, which
       * as the most resent message and end with the oldest
       * messages at the front of the queue.
       */
      for (int i = mDeferredMessages.size() - 1; i >= 0; i-- ) {
        Message curMsg = mDeferredMessages.get(i);
        if (mDbg) Log.d(TAG, "moveDeferredMessageAtFrontOfQueue; what=" + curMsg.what);
        sendMessageAtFrontOfQueue(curMsg);
      }
      mDeferredMessages.clear();
    }
    /**
     * Move the contents of the temporary stack to the state stack
     * reversing the order of the items on the temporary stack as
     * they are moved.
     *
     * @return index into mStateStack where entering needs to start
     */
    private final int moveTempStateStackToStateStack() {
      int startingIndex = mStateStackTopIndex + 1;
      int i = mTempStateStackCount - 1;
      int j = startingIndex;
      while (i >= 0) {
        if (mDbg) Log.d(TAG, "moveTempStackToStateStack: i=" + i + ",j=" + j);
        mStateStack[j] = mTempStateStack[i];
        j += 1;
        i -= 1;
      }
      mStateStackTopIndex = j - 1;
      if (mDbg) {
        Log.d(TAG, "moveTempStackToStateStack: X mStateStackTop="
            + mStateStackTopIndex + ",startingIndex=" + startingIndex
            + ",Top=" + mStateStack[mStateStackTopIndex].state.getName());
      }
      return startingIndex;
    }
    /**
     * Setup the mTempStateStack with the states we are going to enter.
     *
     * This is found by searching up the destState's ancestors for a
     * state that is already active i.e. StateInfo.active == true.
     * The destStae and all of its inactive parents will be on the
     * TempStateStack as the list of states to enter.
     *
     * @return StateInfo of the common ancestor for the destState and
     * current state or null if there is no common parent.
     */
    private final StateInfo setupTempStateStackWithStatesToEnter(State destState) {
      /**
       * Search up the parent list of the destination state for an active
       * state. Use a do while() loop as the destState must always be entered
       * even if it is active. This can happen if we are exiting/entering
       * the current state.
       */
      mTempStateStackCount = 0;
      StateInfo curStateInfo = mStateInfo.get(destState);
      do {
        mTempStateStack[mTempStateStackCount++] = curStateInfo;
        curStateInfo = curStateInfo.parentStateInfo;
      } while ((curStateInfo != null) && !curStateInfo.active);
      if (mDbg) {
        Log.d(TAG, "setupTempStateStackWithStatesToEnter: X mTempStateStackCount="
            + mTempStateStackCount + ",curStateInfo: " + curStateInfo);
      }
      return curStateInfo;
    }
    /**
     * Initialize StateStack to mInitialState.
     */
    private final void setupInitialStateStack() {
      if (mDbg) {
        Log.d(TAG, "setupInitialStateStack: E mInitialState="
            + mInitialState.getName());
      }
      StateInfo curStateInfo = mStateInfo.get(mInitialState);
      for (mTempStateStackCount = 0; curStateInfo != null; mTempStateStackCount++) {
        mTempStateStack[mTempStateStackCount] = curStateInfo;
        curStateInfo = curStateInfo.parentStateInfo;
      }
      // Empty the StateStack
      mStateStackTopIndex = -1;
      moveTempStateStackToStateStack();
    }
    /**
     * @return current message
     */
    private final Message getCurrentMessage() {
      return mMsg;
    }
    /**
     * @return current state
     */
    private final IState getCurrentState() {
      return mStateStack[mStateStackTopIndex].state;
    }
    /**
     * Add a new state to the state machine. Bottom up addition
     * of states is allowed but the same state may only exist
     * in one hierarchy.
     *
     * @param state the state to add
     * @param parent the parent of state
     * @return stateInfo for this state
     */
    private final StateInfo addState(State state, State parent) {
      if (mDbg) {
        Log.d(TAG, "addStateInternal: E state=" + state.getName()
            + ",parent=" + ((parent == null) ? "" : parent.getName()));
      }
      StateInfo parentStateInfo = null;
      if (parent != null) {
        parentStateInfo = mStateInfo.get(parent);
        if (parentStateInfo == null) {
          // Recursively add our parent as it's not been added yet.
          parentStateInfo = addState(parent, null);
        }
      }
      StateInfo stateInfo = mStateInfo.get(state);
      if (stateInfo == null) {
        stateInfo = new StateInfo();
        mStateInfo.put(state, stateInfo);
      }
      // Validate that we aren't adding the same state in two different hierarchies.
      if ((stateInfo.parentStateInfo != null) &&
          (stateInfo.parentStateInfo != parentStateInfo)) {
        throw new RuntimeException("state already added");
      }
      stateInfo.state = state;
      stateInfo.parentStateInfo = parentStateInfo;
      stateInfo.active = false;
      if (mDbg) Log.d(TAG, "addStateInternal: X stateInfo: " + stateInfo);
      return stateInfo;
    }
    /**
     * Constructor
     *
     * @param looper for dispatching messages
     * @param sm the hierarchical state machine
     */
    private SmHandler(Looper looper, StateMachine sm) {
      super(looper);
      mSm = sm;
      addState(mHaltingState, null);
      addState(mQuittingState, null);
    }
    /** @see StateMachine#setInitialState(State) */
    private final void setInitialState(State initialState) {
      if (mDbg) Log.d(TAG, "setInitialState: initialState=" + initialState.getName());
      mInitialState = initialState;
    }
    /** @see StateMachine#transitionTo(IState) */
    private final void transitionTo(IState destState) {
      mDestState = (State) destState;
      if (mDbg) Log.d(TAG, "transitionTo: destState=" + mDestState.getName());
    }
    /** @see StateMachine#deferMessage(Message) */
    private final void deferMessage(Message msg) {
      if (mDbg) Log.d(TAG, "deferMessage: msg=" + msg.what);
      /* Copy the "msg" to "newMsg" as "msg" will be recycled */
      Message newMsg = obtainMessage();
      newMsg.copyFrom(msg);
      mDeferredMessages.add(newMsg);
    }
    /** @see StateMachine#quit() */
    private final void quit() {
      if (mDbg) Log.d(TAG, "quit:");
      sendMessage(obtainMessage(SM_QUIT_CMD, mSmHandlerObj));
    }
    /** @see StateMachine#quitNow() */
    private final void quitNow() {
      if (mDbg) Log.d(TAG, "abort:");
      sendMessageAtFrontOfQueue(obtainMessage(SM_QUIT_CMD, mSmHandlerObj));
    }
    /** Validate that the message was sent by quit or abort. */
    private final boolean isQuit(Message msg) {
      return (msg.what == SM_QUIT_CMD) && (msg.obj == mSmHandlerObj);
    }
    /** @see StateMachine#isDbg() */
    private final boolean isDbg() {
      return mDbg;
    }
    /** @see StateMachine#setDbg(boolean) */
    private final void setDbg(boolean dbg) {
      mDbg = dbg;
    }
  }
  private SmHandler mSmHandler;
  private HandlerThread mSmThread;
  /**
   * Initialize.
   *
   * @param looper for this state machine
   * @param name of the state machine
   */
  private void initStateMachine(String name, Looper looper) {
    mName = name;
    mSmHandler = new SmHandler(looper, this);
  }
  /**
   * Constructor creates a StateMachine with its own thread.
   *
   * @param name of the state machine
   */
  protected StateMachine(String name) {
    mSmThread = new HandlerThread(name);
    mSmThread.start();
    Looper looper = mSmThread.getLooper();
    initStateMachine(name, looper);
  }
  /**
   * Constructor creates a StateMachine using the looper.
   *
   * @param name of the state machine
   */
  protected StateMachine(String name, Looper looper) {
    initStateMachine(name, looper);
  }
  /**
   * Add a new state to the state machine
   * @param state the state to add
   * @param parent the parent of state
   */
  protected final void addState(State state, State parent) {
    mSmHandler.addState(state, parent);
  }
  /**
   * @return current message
   */
  protected final Message getCurrentMessage() {
    return mSmHandler.getCurrentMessage();
  }
  /**
   * @return current state
   */
  protected final IState getCurrentState() {
    return mSmHandler.getCurrentState();
  }
  /**
   * Add a new state to the state machine, parent will be null
   * @param state to add
   */
  protected final void addState(State state) {
    mSmHandler.addState(state, null);
  }
  /**
   * Set the initial state. This must be invoked before
   * and messages are sent to the state machine.
   *
   * @param initialState is the state which will receive the first message.
   */
  protected final void setInitialState(State initialState) {
    mSmHandler.setInitialState(initialState);
  }
  /**
   * transition to destination state. Upon returning
   * from processMessage the current state's exit will
   * be executed and upon the next message arriving
   * destState.enter will be invoked.
   *
   * this function can also be called inside the enter function of the
   * previous transition target, but the behavior is undefined when it is
   * called mid-way through a previous transition (for example, calling this
   * in the enter() routine of a intermediate node when the current transition
   * target is one of the nodes descendants).
   *
   * @param destState will be the state that receives the next message.
   */
  protected final void transitionTo(IState destState) {
    mSmHandler.transitionTo(destState);
  }
  /**
   * transition to halt state. Upon returning
   * from processMessage we will exit all current
   * states, execute the onHalting() method and then
   * for all subsequent messages haltedProcessMessage
   * will be called.
   */
  protected final void transitionToHaltingState() {
    mSmHandler.transitionTo(mSmHandler.mHaltingState);
  }
  /**
   * Defer this message until next state transition.
   * Upon transitioning all deferred messages will be
   * placed on the queue and reprocessed in the original
   * order. (i.e. The next state the oldest messages will
   * be processed first)
   *
   * @param msg is deferred until the next transition.
   */
  protected final void deferMessage(Message msg) {
    mSmHandler.deferMessage(msg);
  }
  /**
   * Called when message wasn't handled
   *
   * @param msg that couldn't be handled.
   */
  protected void unhandledMessage(Message msg) {
    if (mSmHandler.mDbg) Log.e(TAG, mName + " - unhandledMessage: msg.what=" + msg.what);
  }
  /**
   * Called for any message that is received after
   * transitionToHalting is called.
   */
  protected void haltedProcessMessage(Message msg) {
  }
  /**
   * This will be called once after handling a message that called
   * transitionToHalting. All subsequent messages will invoke
   * {@link StateMachine#haltedProcessMessage(Message)}
   */
  protected void onHalting() {
  }
  /**
   * This will be called once after a quit message that was NOT handled by
   * the derived StateMachine. The StateMachine will stop and any subsequent messages will be
   * ignored. In addition, if this StateMachine created the thread, the thread will
   * be stopped after this method returns.
   */
  protected void onQuitting() {
  }
  /**
   * @return the name
   */
  public final String getName() {
    return mName;
  }
  /**
   * Set number of log records to maintain and clears all current records.
   *
   * @param maxSize number of messages to maintain at anyone time.
   */
  public final void setLogRecSize(int maxSize) {
    mSmHandler.mLogRecords.setSize(maxSize);
  }
  /**
   * @return number of log records
   */
  public final int getLogRecSize() {
    return mSmHandler.mLogRecords.size();
  }
  /**
   * @return the total number of records processed
   */
  public final int getLogRecCount() {
    return mSmHandler.mLogRecords.count();
  }
  /**
   * @return a log record
   */
  public final LogRec getLogRec(int index) {
    return mSmHandler.mLogRecords.get(index);
  }
  /**
   * Add the string to LogRecords.
   *
   * @param string
   */
  protected void addLogRec(String string) {
    mSmHandler.mLogRecords.add(null, string, null, null);
  }
  /**
   * Add the string and state to LogRecords
   *
   * @param string
   * @param state current state
   */
  protected void addLogRec(String string, State state) {
    mSmHandler.mLogRecords.add(null, string, state, null);
  }
  /**
   * @return true if msg should be saved in the log, default is true.
   */
  protected boolean recordLogRec(Message msg) {
    return true;
  }
  /**
   * Return a string to be logged by LogRec, default
   * is an empty string. Override if additional information is desired.
   *
   * @param msg that was processed
   * @return information to be logged as a String
   */
  protected String getLogRecString(Message msg) {
    return "";
  }
  /**
   * @return the string for msg.what
   */
  protected String getWhatToString(int what) {
    return null;
  }
  /**
   * @return Handler
   */
  public final Handler getHandler() {
    return mSmHandler;
  }
  /**
   * Get a message and set Message.target = this.
   *
   * @return message or null if SM has quit
   */
  public final Message obtainMessage()
  {
    if (mSmHandler == null) return null;
    return Message.obtain(mSmHandler);
  }
  /**
   * Get a message and set Message.target = this and what
   *
   * @param what is the assigned to Message.what.
   * @return message or null if SM has quit
   */
  public final Message obtainMessage(int what) {
    if (mSmHandler == null) return null;
    return Message.obtain(mSmHandler, what);
  }
  /**
   * Get a message and set Message.target = this,
   * what and obj.
   *
   * @param what is the assigned to Message.what.
   * @param obj is assigned to Message.obj.
   * @return message or null if SM has quit
   */
  public final Message obtainMessage(int what, Object obj)
  {
    if (mSmHandler == null) return null;
    return Message.obtain(mSmHandler, what, obj);
  }
  /**
   * Get a message and set Message.target = this,
   * what, arg1 and arg2
   *
   * @param what  is assigned to Message.what
   * @param arg1  is assigned to Message.arg1
   * @param arg2  is assigned to Message.arg2
   * @return  A Message object from the global pool or null if
   *          SM has quit
   */
  public final Message obtainMessage(int what, int arg1, int arg2)
  {
    if (mSmHandler == null) return null;
    return Message.obtain(mSmHandler, what, arg1, arg2);
  }
  /**
   * Get a message and set Message.target = this,
   * what, arg1, arg2 and obj
   *
   * @param what  is assigned to Message.what
   * @param arg1  is assigned to Message.arg1
   * @param arg2  is assigned to Message.arg2
   * @param obj is assigned to Message.obj
   * @return  A Message object from the global pool or null if
   *          SM has quit
   */
  public final Message obtainMessage(int what, int arg1, int arg2, Object obj)
  {
    if (mSmHandler == null) return null;
    return Message.obtain(mSmHandler, what, arg1, arg2, obj);
  }
  /**
   * Enqueue a message to this state machine.
   */
  public final void sendMessage(int what) {
    // mSmHandler can be null if the state machine has quit.
    if (mSmHandler == null) return;
    mSmHandler.sendMessage(obtainMessage(what));
  }
  /**
   * Enqueue a message to this state machine.
   */
  public final void sendMessage(int what, Object obj) {
    // mSmHandler can be null if the state machine has quit.
    if (mSmHandler == null) return;
    mSmHandler.sendMessage(obtainMessage(what,obj));
  }
  /**
   * Enqueue a message to this state machine.
   */
  public final void sendMessage(Message msg) {
    // mSmHandler can be null if the state machine has quit.
    if (mSmHandler == null) return;
    mSmHandler.sendMessage(msg);
  }
  /**
   * Enqueue a message to this state machine after a delay.
   */
  public final void sendMessageDelayed(int what, long delayMillis) {
    // mSmHandler can be null if the state machine has quit.
    if (mSmHandler == null) return;
    mSmHandler.sendMessageDelayed(obtainMessage(what), delayMillis);
  }
  /**
   * Enqueue a message to this state machine after a delay.
   */
  public final void sendMessageDelayed(int what, Object obj, long delayMillis) {
    // mSmHandler can be null if the state machine has quit.
    if (mSmHandler == null) return;
    mSmHandler.sendMessageDelayed(obtainMessage(what, obj), delayMillis);
  }
  /**
   * Enqueue a message to this state machine after a delay.
   */
  public final void sendMessageDelayed(Message msg, long delayMillis) {
    // mSmHandler can be null if the state machine has quit.
    if (mSmHandler == null) return;
    mSmHandler.sendMessageDelayed(msg, delayMillis);
  }
  /**
   * Enqueue a message to the front of the queue for this state machine.
   * Protected, may only be called by instances of StateMachine.
   */
  protected final void sendMessageAtFrontOfQueue(int what, Object obj) {
    mSmHandler.sendMessageAtFrontOfQueue(obtainMessage(what, obj));
  }
  /**
   * Enqueue a message to the front of the queue for this state machine.
   * Protected, may only be called by instances of StateMachine.
   */
  protected final void sendMessageAtFrontOfQueue(int what) {
    mSmHandler.sendMessageAtFrontOfQueue(obtainMessage(what));
  }
  /**
   * Enqueue a message to the front of the queue for this state machine.
   * Protected, may only be called by instances of StateMachine.
   */
  protected final void sendMessageAtFrontOfQueue(Message msg) {
    mSmHandler.sendMessageAtFrontOfQueue(msg);
  }
  /**
   * Removes a message from the message queue.
   * Protected, may only be called by instances of StateMachine.
   */
  protected final void removeMessages(int what) {
    mSmHandler.removeMessages(what);
  }
  /**
   * Quit the state machine after all currently queued up messages are processed.
   */
  protected final void quit() {
    // mSmHandler can be null if the state machine is already stopped.
    if (mSmHandler == null) return;
    mSmHandler.quit();
  }
  /**
   * Quit the state machine immediately all currently queued messages will be discarded.
   * 注意改方法非线程安全
   */
  protected final void quitNow() {
    // mSmHandler can be null if the state machine is already stopped.
    if (mSmHandler == null) return;
    mSmHandler.quitNow();
  }
  /**
   * @return if debugging is enabled
   */
  public boolean isDbg() {
    // mSmHandler can be null if the state machine has quit.
    if (mSmHandler == null) return false;
    return mSmHandler.isDbg();
  }
  /**
   * Set debug enable/disabled.
   *
   * @param dbg is true to enable debugging.
   */
  public void setDbg(boolean dbg) {
    // mSmHandler can be null if the state machine has quit.
    if (mSmHandler == null) return;
    mSmHandler.setDbg(dbg);
  }
  /**
   * Start the state machine.
   */
  public void start() {
    // mSmHandler can be null if the state machine has quit.
    if (mSmHandler == null) return;
    /** Send the complete construction message */
    mSmHandler.completeConstruction();
  }
  /**
   * Dump the current state.
   *
   * @param fd
   * @param pw
   * @param args
   */
  public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
    pw.println(getName() + ":");
    pw.println(" total records=" + getLogRecCount());
    for (int i=0; i < getLogRecSize(); i++) {
      pw.printf(" rec[%d]: %s\n", i, getLogRec(i).toString(this));
      pw.flush();
    }
    pw.println("curState=" + getCurrentState().getName());
  }
}
