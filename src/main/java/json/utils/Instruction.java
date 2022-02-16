package json.utils;

public class Instruction {
  private String collectionName;
  private String propertyName;
  private String value;

  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getCollectionName() {
    return collectionName;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return getCollectionName() + getPropertyName() + getValue();
  }
}
