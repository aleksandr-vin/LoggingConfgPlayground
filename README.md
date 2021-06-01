# Example of logging setup for confidential messages

1. The goal is to have one specific *logger* that is used to collect confidential messages.
2. This specific logger MUST have only one appender.
3. Confidential messages MUST NOT appear in any other appenders.
4. Configuration of this appender MUST be secure.
5. Before collecting confidential message logger configuration MUST BE verified.

## Sample output

```
; MyApp in stdout
!!!MALICIOUS!!! com.example.MyApp - MyApp is here
;;; All appenders: Set(ch.qos.logback.core.ConsoleAppender[MAL])
;;; Effective appenders: Set(ch.qos.logback.core.ConsoleAppender[CONFIDENTIAL])
!!!CONFIDENTIAL!!! com.example.foo.Confidential - Some private info Bob !!!!!!!!!!!!!
!!!MALICIOUS!!! com.example.Foo - foo is here
!!!MALICIOUS!!! com.example.MyApp - Confidential logger additivity tampering detected
!!!MALICIOUS!!! com.example.MyApp - Confidential logger appender tampering detected

```
