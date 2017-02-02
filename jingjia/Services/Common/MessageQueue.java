package Services.Common;

import java.util.Queue;

/**
 * Created by Administrator on 2017/1/13.
 */
public class MessageQueue {
    private Queue<IMessage> m_queue;// = new Queue<IMessage>();

    public IMessage GetNewMessage(){
        synchronized (m_queue){
            return m_queue.poll();
        }
    }

}
