# Obfuscation/hashing of logs parameters

Provides an SLF4 compatible set of classes to create a logger for hashing logs parameters using HMAC-SHA256 algorithm.
This hashing is a one-way encryption and cannot be reversed, So if users want to log personal information that needs to be used for log correlation the same hash key shall be used.


Configuration :
The Hash key must be setup using a system property (talend.log.obfuscation.key) otherwise the obfuscated data with be replaced by the <hidden> word