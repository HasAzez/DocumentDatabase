package facade;

import java.util.List;

public interface Database extends ReadingPrivileges, WritingPrivileges {

    List<String> getPorts();
}
