package hongwei.javaSE.annotation;

@TableAnnotation("db_student")
public class Student {
    @FieldAnnotation(columnName = "s_id",type = "varchar",length = 64)
    private String id;
    @FieldAnnotation(columnName = "s_name",type = "varchar",length = 20)
    private String name;
    @FieldAnnotation(columnName = "s_age",type = "varchar",length = 3)
    private String age;

    public Student() {
    }

    public Student(String id, String name, String age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
