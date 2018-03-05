package org.elixir_lang.debugger;

import com.ericsson.otp.erlang.*;
import com.intellij.concurrency.AsyncFutureFactory;
import com.intellij.concurrency.AsyncFutureResult;
import com.intellij.openapi.application.ApplicationManager;
import org.elixir_lang.debugger.node.Command;
import org.elixir_lang.debugger.node.command.*;
import org.elixir_lang.debugger.node.event.Listener;
import org.elixir_lang.debugger.node.Exception;
import org.elixir_lang.debugger.node.Event;
import org.elixir_lang.utils.ElixirModulesUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.elixir_lang.beam.term.InspectKt.inspect;
import static org.elixir_lang.debugger.Log.LOG;

public class Node {
  private static final int RECEIVE_TIMEOUT = 50;
  private static final int RETRIES_ON_TIMEOUT = 10;

  private OtpErlangPid myLastSuspendedPid;

  private final Queue<Command> myCommandsQueue = new LinkedList<>();
  private int myLocalDebuggerPort = -1;
  @NotNull
  private final Listener myEventListener;
  @NotNull
  private AtomicBoolean myStopped = new AtomicBoolean(false);

  public Node(@NotNull Listener eventListener) throws Exception {
    myEventListener = eventListener;
    LOG.debug("Starting debugger server.");
    try {
      myLocalDebuggerPort = runDebuggerServer().get();
    }
    catch (Throwable e) {
      throw new Exception("Failed to start debugger server", e);
    }
  }

  public int getLocalDebuggerPort() {
    return myLocalDebuggerPort;
  }

  public void stop() {
    myStopped.set(true);
  }

  @Contract(pure = true)
  private boolean isStopped() {
    return myStopped.get();
  }

  public void processSuspended(OtpErlangPid pid) {
    myLastSuspendedPid = pid;
  }

  public void setBreakpoint(@NotNull String module, @NotNull String file, int line) {
    addCommand(new SetBreakpoint(ElixirModulesUtil.INSTANCE.elixirModuleNameToErlang(module), line, file));
  }

  public void reasonByUninterpretable() {
    addCommand(new ReasonByUninterpretable());
  }

  public void rejectedModuleNames() {
    addCommand(new RejectedModuleNames());
  }

  public void removeBreakpoint(@NotNull String module, int line) {
    addCommand(new RemoveBreakpoint(ElixirModulesUtil.INSTANCE.elixirModuleNameToErlang(module), line));
  }

  public void runTask() {
    addCommand(new RunTask());
  }

  public void stepInto() {
    OtpErlangPid pid = myLastSuspendedPid;
    addCommand(new Pid("step_into", pid));
  }

  public void stepOver() {
    OtpErlangPid pid = myLastSuspendedPid;
    addCommand(new Pid("step_over", pid));
  }

  public void stepOut() {
    OtpErlangPid pid = myLastSuspendedPid;
    addCommand(new Pid("step_out", pid));
  }

  public void resume() {
    OtpErlangPid pid = myLastSuspendedPid;
    addCommand(new Pid("continue", pid));
  }

  private void addCommand(Command command) {
    synchronized (myCommandsQueue) {
      myCommandsQueue.add(command);
    }
  }

  @NotNull
  private Future<Integer> runDebuggerServer() {
    final AsyncFutureResult<Integer> portFuture = AsyncFutureFactory.getInstance().createAsyncFutureResult();
    ApplicationManager.getApplication().executeOnPooledThread(() -> runDebuggerServerImpl(portFuture));
    return portFuture;
  }

  private void runDebuggerServerImpl(@NotNull AsyncFutureResult<Integer> portFuture) {
    try {
      java.lang.Exception cachedException = null;
      LOG.debug("Opening a server socket.");

      try (ServerSocket serverSocket = new ServerSocket(0)) {
        portFuture.set(serverSocket.getLocalPort());

        LOG.debug("Listening on port " + serverSocket.getLocalPort() + ".");

        try (Socket debuggerSocket = serverSocket.accept()) {
          LOG.debug("Event connected, closing the server socket.");
          serverSocket.close();
          myEventListener.debuggerStarted();
          LOG.debug("Starting send/receive loop.");
          serverLoop(debuggerSocket);
        } catch (java.lang.Exception e) {
          cachedException = e;
          throw e;
        } finally {
          myStopped.set(true);
          myEventListener.debuggerStopped();
        }
      } catch (java.lang.Exception e) {
        if (cachedException != null && cachedException != e) {
          if (e.getCause() == null) {
            e.initCause(cachedException);
          } else {
            LOG.debug("Lost exception.", cachedException);
          }
        }
        throw e;
      }
    }
    catch (java.lang.Exception th) {
      if (!portFuture.isDone()) {
        portFuture.setException(th);
      }
      else {
        LOG.debug(th);
      }
    }
  }

  private void serverLoop(@NotNull Socket debuggerSocket) throws IOException {
    debuggerSocket.setSoTimeout(RECEIVE_TIMEOUT);

    while (!isStopped()) {
      if (!isStopped()) {
        receiveMessage(debuggerSocket);
      }
      if (!isStopped()) {
        sendMessages(debuggerSocket);
      }
    }
  }

  private void receiveMessage(@NotNull Socket socket) throws SocketException {
    OtpErlangObject receivedMessage = receive(socket);
    if (receivedMessage == null) return;

    LOG.debug("Message received: " + inspect(receivedMessage));

    Event event = Event.Companion.from(receivedMessage);
    boolean messageRecognized = event != null;
    if (messageRecognized) {
      event.process(this, myEventListener);
    }

    LOG.debug("Message processed: " + messageRecognized);
  }

  private void sendMessages(@NotNull Socket socket) throws SocketException {
    synchronized (myCommandsQueue) {
      while (!myCommandsQueue.isEmpty()) {
        OtpErlangObject message = myCommandsQueue.remove().toMessage();
        LOG.debug("Sending message: " + inspect(message));
        send(socket, message);
      }
    }
  }

  private static void send(@NotNull Socket socket, @NotNull OtpErlangObject message) throws SocketException {
    try {
      OutputStream out = socket.getOutputStream();

      byte[] bytes = new OtpOutputStream(message).toByteArray();
      byte[] sizeBytes = ByteBuffer.allocate(4).putInt(1 + bytes.length).array();

      out.write(sizeBytes);
      out.write(OtpExternal.versionTag);
      out.write(bytes);
    }
    catch (SocketException e) {
      throw e;
    }
    catch (IOException e) {
      LOG.debug(e);
    }
  }

  @Nullable
  private static OtpErlangObject receive(@NotNull Socket socket) throws SocketException {
    try {
      InputStream in = socket.getInputStream();

      int objectSize = readObjectSize(in);
      if (objectSize == -1) return null;

      LOG.debug("Incoming packet size: " + objectSize + " bytes");

      byte[] objectBytes = readBytes(in, objectSize, true);
      return objectBytes == null ? null : decode(objectBytes);
    }
    catch (SocketException e) {
      throw e;
    }
    catch (IOException e) {
      LOG.debug(e);
    }
    return null;
  }

  private static int readObjectSize(@NotNull InputStream in) throws SocketException {
    byte[] bytes = readBytes(in, 4, false);
    return bytes != null ? ByteBuffer.wrap(bytes).getInt() : -1;
  }

  /**
   * Reads size bytes from passed socket input stream.
   * <p/>
   * Makes #RETRIES_ON_TIMEOUT attempts to read input unless the read request is not forced and no bytes were read.
   *
   * @return bytes read or null if the request was not forced and it timed out, or if an I/O exception occurred.
   * @throws SocketException when a socket exception is thrown from passed input stream or if a number of
   *                         retry attempts was exceeded.
   */
  @Nullable
  private static byte[] readBytes(@NotNull InputStream in, int size, boolean force) throws SocketException {
    try {
      int bytesReadTotal = 0;
      byte[] buffer = new byte[size];

      int attemptsMade = 0;
      while (size != bytesReadTotal) {
        try {
          int bytesRead = in.read(buffer, bytesReadTotal, size - bytesReadTotal);
          if (bytesRead < 0) {
            throw new SocketException("A socket was closed.");
          }
          bytesReadTotal += bytesRead;
        }
        catch (SocketTimeoutException e) {
          // if we're not forced to read, but we touched the stream, we're forced
          if (bytesReadTotal == 0 && !force) {
            return null;
          }
          if (++attemptsMade >= RETRIES_ON_TIMEOUT) {
            throw e;
          }
        }
      }
      return buffer;
    }
    catch (SocketException e) {
      throw e;
    }
    catch (IOException e) {
      LOG.debug(e);
      return null;
    }
  }

  @Nullable
  private static OtpErlangObject decode(@NotNull byte[] bytes) {
    try {
      return new OtpInputStream(bytes).read_any();
    }
    catch (OtpErlangDecodeException e) {
      LOG.debug("Failed to decode an erlang term.", e);
      return null;
    }
  }
}
