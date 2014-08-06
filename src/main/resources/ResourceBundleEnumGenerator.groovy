/*
 * The following properties are REQUIRED:
 *
 * resourceBundlePath - the path relative to the Maven's src/main/resources
 *                      directory where the resource bundle files are located.
 * enumPackageName - the package name of the generated Enums
 *
 * The following property is OPTIONAL:
 *
 * interfaceMarkers - comma delimited list of marker interfaces the generated
 *                      enum should implement.
 *
 */
import java.io.*
import java.util.*

def resourcesDir = "src/main/resources";
def javaSrcDir = "src/main/java";

def resourceBundlePath = (String) project.properties['resourceBundlePath']

if (!resourceBundlePath.startsWith("/")) {
    resourceBundlePath = "/" + resourceBundlePath
}
if (!resourceBundlePath.endsWith("/")) {
    resourceBundlePath = resourceBundlePath + "/"
}
resourceBundlePath = resourcesDir + resourceBundlePath

def enumPackageName = (String) project.properties['enumPackageName']
def interfaceMarkers = (String) project.properties['interfaceMarkers']

def rbMap = new HashMap<String, List<String>>()

log.info("Scanning Resource Bundles in directory: " + resourceBundlePath)

new File(resourceBundlePath).eachFile() { file->

    def fileName = file.getName()

    def baseFileName = null

    def baseFileNameDelimiterIndex = fileName.indexOf('_')
    if (baseFileNameDelimiterIndex < 0) {
        baseFileNameDelimiterIndex = fileName.indexOf('.')
    }

    if (baseFileNameDelimiterIndex != -1) {
        baseFileName = fileName.substring(0, baseFileNameDelimiterIndex)
    }

    if (baseFileName != null) {
        def rbFiles = rbMap.get(baseFileName)
        if (rbFiles == null) {
            rbFiles = new ArrayList<String>()
            rbMap.put(baseFileName, rbFiles)
        }

        rbFiles.add(fileName);

        log.info("Found: " + fileName)
    }
}

for (entry in rbMap) {

    def rbName = entry.key
    def rbFileNames = entry.value

    log.info("Generating Enum for bundle: " + rbName)

    def rbKeys = new LinkedHashSet<String>();

    def first = true

    def prevKeyCount = 0

    for (rbFileName in rbFileNames) {

        def keyCount = 0

        def rbFile = new File(resourceBundlePath + rbFileName)

        rbFile.eachLine {
            line ->
                def equalsIndex = line.indexOf('=')
                if (equalsIndex != -1) {

                    keyCount ++

                    def rbKey = line.substring(0, equalsIndex)

                    // println(rbKey)

                    if (rbKeys.add(rbKey) && !first) {
                        log.warn("Warning: Resource bundle key [" + rbKey + "] does not exist in previous bundles!")
                    }
                }
        }

        if (first) {
            first = false

        } else {
            if (prevKeyCount != keyCount) {
                log.warn("Warning: Resource bundle file [" + rbFileName + "] has inconsistent number of keys than previous files!")
            }
        }

        prevKeyCount = keyCount
    }

    // TODO: Add more logic to properly convert the resource bundle name into a proper Java class name
    def enumName = "" + Character.toUpperCase(rbName.charAt(0)) + rbName.substring(1)

    def NL = "\n"
    def TAB = "    "

    // TODO: Handle non-unix based file systems.
    new File(javaSrcDir + "/" + enumPackageName.replaceAll("\\x2e", "/") + "/" + enumName + ".java").withWriter { out ->

        out.write("/* AUTO-GENERATED FILE.  DO NOT MODIFY." + NL)
        out.write(" *" + NL)
        out.write(" * This class was automatically generated on " + new Date() + " by" + NL)
        out.write(" * the Maven build tool based on the Java resource bundles it found. It should" + NL)
        out.write(" * NOT be modified by hand." + NL)
        out.write(" */" + NL)
        out.write("package " + enumPackageName + ";" + NL)
        out.write(NL)
        out.write("import ch.qos.cal10n.BaseName;" + NL)
        out.write("import ch.qos.cal10n.MessageConveyor;" + NL)
        out.write("import ch.qos.cal10n.MessageParameterObj;" + NL)
        out.write(NL)
        out.write("import java.util.Locale;" + NL)
        out.write(NL)
        out.write("@BaseName(\"bundles." + rbName + "\")" + NL)
        out.write("public enum " + enumName + (interfaceMarkers != null ? " implements " + interfaceMarkers : "") + " {" + NL)
        out.write(NL)

        rbKeys.eachWithIndex() { obj, i ->

            // TODO: insert underscore between camel case characters
            def enumIdentifier = obj.replaceAll("[^a-zA-Z0-9]", "_").toUpperCase()

            out.write(TAB + enumIdentifier)

            if (!enumIdentifier.equals(obj)) {
                out.write("(\"" + obj + "\")")
            }

            if (i < rbKeys.size() - 1) {
                out.write(",")

            } else {
                out.write(";")
            }

            out.write(NL)
        };

        out.write(NL)
        out.write(TAB + "private String key;" + NL)
        out.write(NL)
        out.write(TAB + "private " + enumName + "() {" + NL)
        out.write(TAB + "}" + NL)
        out.write(NL)
        out.write(TAB + "private " + enumName + "(String key) {" + NL)
        out.write(TAB + TAB + "this.key = key;" + NL)
        out.write(TAB + "}" + NL)
        out.write(NL)
        out.write(TAB + "public MessageParameterObj format(Object... arguments) {" + NL)
        out.write(TAB + TAB + "return new MessageParameterObj(this, arguments);" + NL)
        out.write(TAB + "}" + NL)
        out.write(NL)
        out.write(TAB + "public String valueFor(Locale locale, Object... arguments) {" + NL)
        out.write(TAB + TAB + "MessageConveyor messageConveyor = MessageConveyorCache.getMessageConveyor(locale);" + NL)
        out.write(TAB + TAB + "if (arguments != null) {" + NL)
        out.write(TAB + TAB + TAB + "return messageConveyor.getMessage(format(arguments));" + NL)
        out.write(TAB + TAB + "} else {" + NL)
        out.write(TAB + TAB + TAB + "return messageConveyor.getMessage(this);" + NL)
        out.write(TAB + TAB + "}" + NL)
        out.write(TAB + "}" + NL)
        out.write(NL)
        out.write(TAB + "@Override" + NL)
        out.write(TAB + "public String toString() {" + NL)
        out.write(TAB + TAB + "return key != null ? key : this.name();" + NL)
        out.write(TAB + "}" + NL)
        out.write("}" + NL)
        out.write(NL)
    }

    log.info("Wrote Class: " + enumPackageName + "." + enumName + ".java")
}

//System.exit(0)
