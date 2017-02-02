package Services.Common;

import java.util.Vector;

/**
 * Created by Administrator on 2017/1/13.
 */
public class MessageDispatcher implements IAsynService {

    private Vector<IAsynWorkFlow> m_workflows;

    public MessageDispatcher(){
        m_workflows = new Vector<IAsynWorkFlow>();
    }

    public void run() {

    }

    public IMessageState Invoke(IMessage message) {
        return null;
    }

    public boolean Process(IMessage message) {
        return false;
    }

    public boolean SetMessageQueue(MessageQueue messageQueue) {
        return false;
    }
}
