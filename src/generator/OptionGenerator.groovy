#!/usr/bin/env groovy

import groovy.transform.SourceURI
import java.nio.file.Paths

// Target files:
// - src/main/java/jp/vmi/selenium/selenese/config/IConfig.java
// - src/main/java/jp/vmi/selenium/selenese/config/DefaultConfig.java
// - src/main/java/jp/vmi/selenium/webdriver/DriverOptions.java

@SourceURI
URI sourceUri

genDir = Paths.get(sourceUri).parent
baseDir = genDir.resolveSibling("main/java/jp/vmi/selenium")
config = new ConfigSlurper().parse(genDir.resolve('config.groovy').toUri().toURL())

INDENT = "        "

def toUpperCamelCase(s) {
  s.replaceAll(/(^|_)([a-z])/) { m -> m[2].toUpperCase() }
}

def toLowerCamelCase(s) {
  s.replaceAll(/(_)([a-z])/) { m -> m[2].toUpperCase() }
}

def replaceBody(path, closure) {
  def body = path.getText()
  body = closure(body)
  path.write(body, "UTF-8")
  println("* Updated: ${path}")
}

def updateOptions(iConfig, dConfig, dOpts) {
  def oNames = "\n"
  def getters = ""
  def fields = ""
  def accessors = ""
  def enumItems = "\n"
  config.forEach { oName, odef ->
    def cName = oName.toUpperCase()
    def uName = toUpperCamelCase(oName)
    def lName = toLowerCamelCase(oName)
    def optStr = oName.replace('_', '-')
    def option = "name = \"--\" + ${cName}"
    def fType = "String"
    def deprecated = false
    odef.forEach { name, value ->
      switch (name) {
        case "deprecated":
	  if (value instanceof Boolean && value) {
	    deprecated = true
	  }
          break

	case "type":
          fType = value
	  break

	case "driverOption":
	  if (value instanceof Boolean && !value) {
	    break
	  }
	  enumItems += "${INDENT}/** --${optStr} */\n"
          if (deprecated) {
            enumItems += "${INDENT}@Deprecated\n"
          }
          enumItems += "${INDENT}${cName}"
	  def t = (value instanceof String) ? value : fType
	  if (t != "String") {
	    enumItems += "(${t}.class)"
	  }
	  enumItems += ",\n"
	  break

	default:
          option += ","
          if (name == "usage") {
            if (option.length() + value.length() + 10 > 180) {
              option += "\n       "
	    }
            value = value.replaceAll(/"/, "\\\\\"").replaceAll(/%(\w+)%/) {
              "\" + ${it[1]} + \""
            }
            if (deprecated) {
              value = "[deprecated] " + value
            }
          }
          option += " ${name} = \"${value}\""
	  break
      }
    }
    def mType = fType
    def mgPrefix = "get"
    def dValue = "null"
    if (fType == "Boolean") {
      mType = "boolean"
      mgPrefix = "is"
      dValue = "false"
    }
    if (deprecated) {
      oNames += "    @Deprecated\n"
    }
    oNames += """\
    public static final String ${cName} = "${optStr}";
"""
    if (deprecated) {
      getters += "\n    @Deprecated"
    }
    getters += """
    ${mType} ${mgPrefix}${uName}();
"""
    if (deprecated) {
      fields += "\n    @Deprecated\n    @SuppressWarnings(\"deprecation\")"
    }
    fields += "\n    @Option(${option})\n    private ${fType} ${lName};\n"
    if (deprecated)  {
      accessors += """
    @Override
    @Deprecated
    public ${mType} ${mgPrefix}${uName}() {
        return ${dValue};
    }
"""
    } else {
      accessors += """
    @Override
    public ${mType} ${mgPrefix}${uName}() {
        return ${lName} != null ? ${lName} : (parentOptions != null ? parentOptions.${mgPrefix}${uName}() : ${dValue});
    }
"""
    }
    if (fType == "String[]") {
      accessors += """
    public void add${uName}(String ${lName}Item) {
        this.${lName} = ArrayUtils.add(this.${lName}, ${lName}Item);
    }
"""
    } else {
      if (deprecated) {
        accessors += """
    @Deprecated
    public void set${uName}(${mType} ${lName}) {
        log.warn("--${optStr} is deprecated.");
    }
"""
      } else {
        accessors += """
    public void set${uName}(${mType} ${lName}) {
        this.${lName} = ${lName};
    }
"""
      }
    }
  }

  replaceBody(iConfig) { body ->
    body.replaceFirst(/(?s)(?<=\/\/ ### BEGIN OPTION NAMES[^\n]*\n).*(?=\n[^\n]*\/\/ ### END OPTION NAMES)/) { oNames }
      .replaceFirst(/(?s)(?<=\/\/ ### BEGIN GETTERS[^\n]*\n).*(?=\n[^\n]*\/\/ ### END GETTERS)/) { getters }
  }
  replaceBody(dConfig) { body ->
    body.replaceFirst(/(?s)(?<=\/\/ ### BEGIN FIELDS[^\n]*\n).*(?=\n[^\n]*\/\/ ### END FIELDS)/) { fields }
      .replaceFirst(/(?s)(?<=\/\/ ### BEGIN GETTERS & SETTERS[^\n]*\n).*(?=\n[^\n]*\/\/ ### END GETTERS & SETTERS)/) { accessors }
  }
  replaceBody(dOpts) { body ->
    body.replaceFirst(/(?s)(?<=\/\/ ### BEGIN ENUM ITEMS[^\n]*\n).*(?=\n[^\n]*\/\/ ### END ENUM ITEMS)/) { enumItems }
  }
}

updateOptions(
  baseDir.resolve("selenese/config/IConfig.java"),
  baseDir.resolve("selenese/config/DefaultConfig.java"),
  baseDir.resolve("webdriver/DriverOptions.java")
)

// Local Variables:
// groovy-indent-offset: 2
// End:
