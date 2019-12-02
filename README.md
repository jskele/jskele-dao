# jskele-dao

 
### Configuration

#### Installing
See [these instruction](https://jitpack.io/#jskele/lib-dao)
for adding this library as a dependency to your project.

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

#### Make `@Dao` interfaces discoverable for this library

There are two options how to make your `@Dao` classes discoverable for this project

##### Option a) Set up spring-component-indexer
As this library contains `META-INF/spring.components`,
Spring 5 doesn't perform slow classpath scanning and assumes all Spring bean candidates are available via `spring.components` files.
To generate `spring.components` file for components in your own project as well, add dependency to
[spring-context-indexer](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-scanning-index).

##### Option b) Provide base package names of `@Dao` classes for component scanning
If you can't use component indexer
(for example because one of the libraries in your project relies on classpath scanning)
you can
1) disable Spring 5 component indexing via `spring.properties` in the classpath:
`spring.index.ignore=true`
2) create bean that provides base packages for your `@Dao` classes:
```Java
    @Bean(org.jskele.dao.impl.DaoRegistrator.JSKELE_DAO_PACKAGES_BEAN_NAME)
    String[] jskeleDaoPackages() {
        return new String[]{"com.example.myproject.dao"};
    }
```
3) Add runtime dependency
`org.reflections:reflections:0.9.11`
that is needed when using this option to find classes from packages you provide.


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
