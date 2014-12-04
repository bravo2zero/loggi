Loggi - logs parsing and analysing tool
=======================================

Overview
--------
Inspired by countless hours spent fishing something useful out of the text logs, I've decided to put this problem to an end. There are a bunch of other solutions to this issue, but I find them either too bulky, or not customizable or even non-free :)

How to build?
-------------

*Prerequisites:*
* JDK 7
* Maven

1. Get a copy of the main git repo:

  ```bash
  git clone https://github.com/CptSpaetzle/loggi.git
  ```

2. Build loggi using Maven:

  ``` bash
  mvn clean install
  ```

3. You will find the final jar package in `${project.base.dir}/target/` folder.

How to use?
-----------
Loggi is a command line tool to analyse practically any log file. The idea is to write a json based template configuration file to let loggi know how exactly you need the log file to be parsed:
* Create template file according to your log format and requirements. Check example [templates](https://github.com/CptSpaetzle/loggi/tree/master/templates) and see Wiki documentation on [Template File Format](https://github.com/CptSpaetzle/loggi/wiki/Template-File-Format)
* Run loggi, providing source log file:

  ``` bash
  java -jar loggi.jar -t ${template.json.file} -s ${source.log.file}
  ```
* Once you see a prompt, you can access results through browser (default *http://localhost:8082*):
<pre>
JDBC URL: jdbc:h2:mem:loggi
User: user
Password: password
</pre>
* Use H2 Console to manipulate your logs and make further analisys (table 'records').
* Get more help on loggi from [command-line](https://github.com/CptSpaetzle/loggi/wiki/Help) (parameters, currently available processor types and configuration)

How to contribute?
------------------
Of course the main reason to put Loggi on GitHub was to support the spirit of an open source software development, letting as much people as possible to use and improve this tool. Please feel free to contact [author](https://github.com/CptSpaetzle) if you want to become a Contributor. Any other kind of feedback is very much appreciated.
