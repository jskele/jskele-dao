# lib-dao

 
### Configuration
 
 
**By default** application bean base package is used to scan for Dao classes. 
 
`jskele.dao.packages` optional property can be used to specify custom list of packages to scan.

```
jskele.dao.packages=com.package1,org.package2
```

It is possible to specify database schema. It is applied to current and child interfaces generated SQL statements
```
@Dao(schema="myDatabaseSchema")
```

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
