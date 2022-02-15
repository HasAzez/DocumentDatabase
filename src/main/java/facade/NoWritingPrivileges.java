package facade;

import java.io.File;
import java.io.IOException;

public class NoWritingPrivileges implements WritingPrivileges {
    @Override
    public String createCollection(String collectionName, String jsonSchema) throws IOException {
        return noWritingPrivileges();
    }

    @Override
    public String deleteCollection() throws IOException {
        return noWritingPrivileges();
    }

    @Override
    public String add(String jsonString) throws IOException {
        return noWritingPrivileges();
    }

    @Override
    public String delete(String property, String value) throws IOException {
        return noWritingPrivileges();
    }

    @Override
    public String update(String property, String value, String jsonString) throws IOException {
        return noWritingPrivileges();
    }

    @Override
    public String selectCollection(String collectionName) {
        return noWritingPrivileges();
    }

    @Override
    public String dumpCollection() throws IOException {
        return noWritingPrivileges();
    }

    @Override
    public String importCollection(File file) throws IOException, ClassNotFoundException {
        return noWritingPrivileges();
    }

    @Override
    public String makeIndexOn(String propertyName) {
        return noWritingPrivileges();
    }
    public String noWritingPrivileges() {
        return "this User doesnt have writing privileges";
    }
}
