package Services.Common;

/**
 * Created by Administrator on 2017/1/13.
 */
public class ForkWorkflow implements IAsynWorkFlow {
    public IMessage Process(IMessage message) {
        return null;
    }

    public boolean RegisterService(IService service) {
        return false;
    }

    public int ForkNumber = 0;
    private IService _service;
}
