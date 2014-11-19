package proto;
import proto.generated.AddressBookProtos.AddressBook;
import proto.generated.AddressBookProtos.Person;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

class AddPerson {
  static Person newPerson() {
    Person.Builder person = Person.newBuilder();

    person.setId(1);
    person.setName("Mike");
    person.setEmail("mike@abc.com");

    Person.PhoneNumber.Builder phoneNumber =
        Person.PhoneNumber.newBuilder().setNumber("12388888888");
      phoneNumber.setType(Person.PhoneType.MOBILE);
      person.addPhone(phoneNumber);

    return person.build();
  }
  
  static Person newPerson(int id, String name, String email, String phone) {
    Person.Builder person = Person.newBuilder();

    person.setId(id);
    person.setName(name);
    person.setEmail(email);

    Person.PhoneNumber.Builder phoneNumber =
        Person.PhoneNumber.newBuilder().setNumber(phone);
      phoneNumber.setType(Person.PhoneType.MOBILE);
      person.addPhone(phoneNumber);

    return person.build();
  }

  public static void main(String[] args) throws Exception {
    AddressBook.Builder addressBook = AddressBook.newBuilder();

    // Read the existing address book.
    try {
      addressBook.mergeFrom(new FileInputStream(args[0]));
    } catch (FileNotFoundException e) {
      System.out.println(args[0] + ": File not found.  Creating a new file.");
    }

    // Add an address.
    addressBook.addPerson(AddPerson.newPerson());

    // Write the new address book back to disk.
    FileOutputStream output = new FileOutputStream(args[0]);
    addressBook.build().writeTo(output);
    output.close();
  }
}