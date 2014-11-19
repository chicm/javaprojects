package proto;

import java.util.HashMap;

import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import proto.generated.AddressBookProtos.Name;
import proto.generated.AddressBookProtos.Person;
import proto.generated.AddressBookProtos.PersonService;

public class PersonServiceHandler implements PersonService.BlockingInterface {
  private static HashMap<String, Person> map = new HashMap<String, Person>();  

  @Override
  public Person search(RpcController controller, Name request) {
    // TODO Auto-generated method stub
    Person p = map.get(request.getName());
    return p;
  }

  @Override
  public Person add(RpcController controller, Person request) {
    // TODO Auto-generated method stub
    map.put(request.getName(), request);
    return request;
  }
  
  public BlockingService getService() {
    return PersonService.newReflectiveBlockingService(this);
  }
  
  public String getMap() {
    return map.toString();
  }

}
