import groovy.json.JsonSlurper

def getGitHubDevBranches(String prodPreFix="al8-20220901", String devRepo="almalinuxautobot/docker-images"){
  def regions = "curl -s https://api.github.com/repos/${devRepo}/branches"
  def region = regions.execute() 
  region.waitFor()

  def output = region.in.text
  def exitcode= region.exitValue()
  def error = region.err.text
  def refString = ""
  if (error) {
        println "Std Err: ${error}"
        println "Process exit code: ${exitcode}"
        return exitcode
  }

  def jsonSlurper = new JsonSlurper()
  def parseJson = jsonSlurper.parseText(output)
  def result = []
  def prefix = prodPreFix.substring(0,3)
  for (str in parseJson['name']) {
    if ((str.startsWith(prefix) && str > prodPreFix) && !str.contains("template")) {
      result.add(str)
    }
  }

  // println(result)
  return result
}

def getGitHubProdBranch(String osVer="al8", String prodRepoWithBranch="docker-library/official-images/master"){
  def regions = "curl -s https://raw.githubusercontent.com/${prodRepoWithBranch}/library/almalinux"
  def region = regions.execute() 
  region.waitFor()

  def output = region.in.text
  def exitcode= region.exitValue()
  def error = region.err.text
  def refString = ""

  if (error) {
        println "Std Err: ${error}"
        println "Process exit code: ${exitcode}"
        return exitcode
  }

  def branch=""

  for(i in output.tokenize()){
    if (i.endsWith("amd64") && i.contains(osVer)) {
      branch = i.substring(11,23)
      break
    }
  }

  return branch
}

def getBranchesAlma8() {
  def a1 = getGitHubProdBranch()  // parameter al8 is default, defaults to standard repos
  def a2 = getGitHubDevBranches(a1)

  def a3 = []
  a3.add(a1)
  a3.addAll(a2)

  return a3
}
def getBranchesAlma9() {
  def a1 = getGitHubProdBranch("al9")  // defaults to standard repos
  def a2 = getGitHubDevBranches(a1)

  def a3 = []
  a3.add(a1)
  a3.addAll(a2)

  return a3
}