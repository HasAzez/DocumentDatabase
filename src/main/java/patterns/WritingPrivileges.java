package patterns;

import json.utils.Instruction;

import java.io.File;
import java.io.IOException;

public interface WritingPrivileges {

  String createCollection(String collectionName, String jsonSchema) throws IOException;

  String deleteCollection(String collectionName) throws IOException;

  String add(String collectionName,String jsonString) throws IOException;

   String delete(Instruction instruction)throws IOException;

   String update(Instruction instruction, String jsonString) throws IOException;


  String dumpCollection(String collectionName) throws IOException;

  String importCollection(String collectionName,File file) throws IOException, ClassNotFoundException;

  String makeIndexOn(String collectionName,String propertyName);
}
