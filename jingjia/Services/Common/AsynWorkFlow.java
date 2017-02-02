package Services.Common;



import java.util.Vector;

/**
 * Created by Administrator on 2017/1/13.
 */
public class AsynWorkFlow implements IMessageHandler {
    private Vector<IService> m_services;
    private MessageQueue m_messageQueue = null;


    public boolean RegisterService(IService service)
    {
        return m_services.add(service);
    }

    Vector<IService> GetServices()
    {
        return m_services;
    }


    public boolean SetMessageQueue(MessageQueue messageQueue) {
        messageQueue = messageQueue;
        return true;
    }

    public void run() {

        try{
            m_messageQueue.wait();
        }catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        IMessage message = m_messageQueue.GetNewMessage();
        for(int i=0; i< m_services.size(); i++){
            ((IService)m_services.get(i)).Process(message);
        }

    }


}
