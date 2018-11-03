# Content service
http://www.talend.com


![alt text](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")

This module contains the source files for a generic content service with multiple file system storages. The currently supported file systems are:
* Local file system
* Amazon S3

## Folders description
| _Module_                                           | _Description_                                                        |
|:---------------------------------------------------|------------------------------------------|
| [content-service-common](content-service-common)   | *Generic content service description*    |
| [local-content-service](local-content-service)     | *Adds local file system capabilities*    |
| [s3-content-service](s3-content-service)           | *Adds AWS S3 capabilities*               |
| [blob-content-service](blob-content-service)       | *Adds Azure Blob capabilities*           |

Some providers (especially cloud storage providers such as S3 or Blob) are not very efficient when performing pattern-based search of resources. They then rely on list operations that are known to be slow as well as memory greedy.

To allow fast search on these providers, optional modules such as:

| _Module_                                           | _Description_                                                        |
|:---------------------------------------------------|------------------------------------------|
| [content-service-journal](content-service-journal) | *Generic content service journal support*|
| [content-service-journal-mongo](local-content-service)| *MongoDB based journal implementation*|

These modules do:

- Creation and initial indexing of all stored object names.
- Once index is completed, perform all search operations using index.

## Usage

To use local file system, see [local-content-service](local-content-service) for configuration.

To use S3, see [s3-content-service](s3-content-service) for configuration.