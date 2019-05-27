# lib-dao

 
### Configuration

#### Add Java compiler option `-parameters`
Adding Java compiler option `-parameters` is needed,
so that lib-dao could figure out parameter names when generating SQL
based on Dao interface method name or Java class representing table row.

> Generates metadata for reflection on method parameters.
> Stores formal parameter names of constructors and methods in the generated class file
> so that the method java.lang.reflect.Executable.getParameters
> from the Reflection API can retrieve them.

For example in case of Gradle, following configuration will do the trick.
```gradle
tasks.withType(JavaCompile) {
    options.compilerArgs << '-parameters'
}
```

Also configure Your IDE.

For example in case of IntelliJ IDEA:

add `-parameters` to
Settings -> Build, Execution, Deployment -> Compiler -> Java Compiler -> Additional command line parameters

#### Specify base package used for scanning for Dao interfaces
 
**By default** application bean base package is used to scan for Dao classes. 
 
`jskele.dao.packages` optional property can be used to specify custom list of packages to scan.

```
jskele.dao.packages=com.package1,org.package2
```

#### Specify database schema

Database schema is resolved for each Dao class based using `DbSchemaResolver` bean.
Add custom implementation of it to change default DB schema resolving if needed.
By default
`@Dao(schema="myDatabaseSchema")`
can be used to set custom DB schema for single Dao class.


### Supported generated SQL statements

The following prefixes can be used in method naming, in conjunction with `@GenerateSql` annotation.

* insert
* update
* delete
* exists
* count
* selectForUpdate


### Examples

```
@GenerateSql
List<MyDto> selectBySomeAttributes(Long id, String fieldName);
```
