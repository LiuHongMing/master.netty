package action.rpc.simple.server;

import java.io.Serializable;

public class RpcResponse implements Serializable {

    private String requestId;
    private Throwable error;
    private Object result;

    private boolean isError;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
        setIsError(true);
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public String toString() {
        return requestId + "," + error + "," + result;
    }
}
