package patterns;

import json.utils.Instruction;

import java.io.File;
import java.io.IOException;

public class NoWritingPrivileges implements WritingPrivileges {
    @Override
    public String createCollection(String collectionName, String jsonSchema)  {
        return noWritingPrivileges();
    }

    @Override
    public String deleteCollection(String collectionName){
        return noWritingPrivileges();
    }

    @Override
    public String add(String collectionName, String jsonString) throws IOException {
        return noWritingPrivileges();
    }

    @Override
    public String delete(Instruction instruction) {
        return noWritingPrivileges();
    }

    @Override
    public String update(Instruction instruction, String jsonString){
        return noWritingPrivileges();
    }


    @Override
    public String dumpCollection(String collectionName){
        return noWritingPrivileges();
    }

    @Override
    public String importCollection(String collectionName, File file)  {
        return noWritingPrivileges();
    }

    @Override
    public String makeIndexOn(String collectionName, String propertyName) {
        return noWritingPrivileges();
    }


    public String noWritingPrivileges() {
        return "this User doesnt have writing privileges";
    }
}
