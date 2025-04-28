/**
*  Remote Builder Storage Driver
*  Version: See ChangeLog
*  Download: See importUrl in definition
*  Description: Used in conjunction with the Remote Builder app to store remotes for publishing to the dashboard.
*
*  Copyright 2024 Gary J. Milne  
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.

*  License:
*  You are free to use this software in an un-modified form. Software cannot be modified or redistributed.
*  You may use the code for educational purposes or for use within other applications as long as they are unrelated to the 
*  production of tabular data in HTML form, unless you have the prior consent of the author.
*  Use of Remote Builder requires a license key that must be issued to you by the original developer. TileBuilderApp@gmail.com
*  License keys are interchangeable between Tile Builder and Remote Builder.
*
*  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
*  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
*
*  Authors Notes:
*  For more information on Remote Builder check out these resources.
*  Original posting on Hubitat Community forum: TBD
*  Remote Builder Documentation: TBD
*
*  Remote Builder Storage Driver - ChangeLog
*  
*  Gary Milne - April 27th, 2025
*  
*  Initial Release V 1.0
*  Version 1.0.1 - Minor errata corrected.
*  Version 1.0.2 - Added function createTile2() for use with SmartGrid 4.0
*  Version 1.0.3 - Fixed bug in deleting Attributes 21 - 25.
*  Version 1.0.4 - Added lastUpdate feature similar to Tile Builder now that SmartGrid gets modified more frequently.
*
**/

metadata {
	definition (name: "Remote Builder Storage Driver", namespace: "garyjmilne", author: "garymilne", importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_Storage_Driver.groovy", singleThreaded: true ) {
        capability "Variable"
    }
    
	capability "Refresh"
    attribute "Remote1-Local", "string"
	attribute "Remote1-Cloud", "string"
	attribute "Remote2-Local", "string"
	attribute "Remote2-Cloud", "string"
	attribute "Remote3-Local", "string"
	attribute "Remote3-Cloud", "string"
	attribute "Remote4-Local", "string"
	attribute "Remote4-Cloud", "string"
	attribute "Remote5-Local", "string"
	attribute "Remote5-Cloud", "string"
	attribute "Remote6-Local", "string"
	attribute "Remote6-Cloud", "string"
	attribute "Remote7-Local", "string"
	attribute "Remote7-Cloud", "string"
	attribute "Remote8-Local", "string"
	attribute "Remote8-Cloud", "string"
	attribute "Remote9-Local", "string"
	attribute "Remote9-Cloud", "string"
	attribute "Remote10-Local", "string"
	attribute "Remote10-Cloud", "string"
	attribute "Remote11-Local", "string"
	attribute "Remote11-Cloud", "string"
	attribute "Remote12-Local", "string"
	attribute "Remote12-Cloud", "string"
	attribute "Remote13-Local", "string"
	attribute "Remote13-Cloud", "string"
	attribute "Remote14-Local", "string"
	attribute "Remote14-Cloud", "string"
	attribute "Remote15-Local", "string"
	attribute "Remote15-Cloud", "string"
	attribute "Remote16-Local", "string"
	attribute "Remote16-Cloud", "string"
	attribute "Remote17-Local", "string"
	attribute "Remote17-Cloud", "string"
	attribute "Remote18-Local", "string"
	attribute "Remote18-Cloud", "string"
	attribute "Remote19-Local", "string"
	attribute "Remote19-Cloud", "string"
	attribute "Remote20-Local", "string"
	attribute "Remote20-Cloud", "string"
    attribute "Remote21-Local-A", "string"
    attribute "Remote21-Local-B", "string"
	attribute "Remote21-Cloud-A", "string"
    attribute "Remote21-Cloud-B", "string"
    attribute "Remote22-Local-A", "string"
    attribute "Remote22-Local-B", "string"
	attribute "Remote22-Cloud-A", "string"
    attribute "Remote22-Cloud-B", "string"
    attribute "Remote23-Local-A", "string"
    attribute "Remote23-Local-B", "string"
	attribute "Remote23-Cloud-A", "string"
    attribute "Remote23-Cloud-B", "string"
    attribute "Remote24-Local-A", "string"
    attribute "Remote24-Local-B", "string"
	attribute "Remote24-Cloud-A", "string"
    attribute "Remote24-Cloud-B", "string"
    attribute "Remote25-Local-A", "string"
    attribute "Remote25-Local-B", "string"
	attribute "Remote25-Cloud-A", "string"
    attribute "Remote25-Cloud-B", "string"
	
	//command "test"
    command "initialize"
    command "createTile", [ [name:"The tile number.*" , type: "NUMBER" , description: "Valid entries are '1 - 25'", range: 1..25], [name:"The Local iFrame.*" , type: "STRING", description: "The local iFrame created by Remote Builder" ], \
						   [name:"The Cloud iFrame.*" , type: "STRING", description: "The Cloud iFrame created by Remote Builder" ], [name:"Tile Description." , type: "STRING" , description: "i.e. 'Living Room Remote'"] ]
    command "deleteTile", [ [name:"The tile number.*", type: "NUMBER", description: "Valid entries are '1 - 25'", range: 1..26] ]
    command "setTileDescription", [ [name:"The tile number.*" , type: "NUMBER" , description: "Valid entries are '1 - 25'", range: 1..25], [name:"Tile Description." , type: "STRING" , description: "i.e. 'Living Room Remote'"] ]
}

section("Configure the Inputs"){
    input name: "logging_level", type: "number", title: bold("Level of detail displayed in log. 0 - Delete, 1 - Delete\\Create, 2 - Delete\\Create\\Update."), description: italic("Enter log level 0-2. (Default is 0.)"), defaultValue: "0", required:true, displayDuringSetup: false            
    } 

void installed() {
   log.debug "installed()"
   initialize()
}

void updated() {
   log.debug "updated()"
}

void test(){
	//sendEvent(name: "emptyAttribute", value: " ") 
    //state.remove("")
}

//Creates a tile
void createTile(tileNumber, HTML1, HTML2, description) {
    log("createTile", "Publishing tile: $tileNumber - $description", 1)
    tileName1 = "Remote" + tileNumber.toString() + "-Local"
	tileName2 = "Remote" + tileNumber.toString() + "-Cloud"
    if (state.tileDescriptions == null) state.tileDescriptions = [:]
	state.tileDescriptions."${tileName1}" = description
	state.tileDescriptions."${tileName2}" = description
	sendEvent(name: tileName1, value: toHTML(HTML1) )
	sendEvent(name: tileName2, value: toHTML(HTML2) )
	state."${tileName1}" = HTML1
	state."${tileName2}" = HTML2
    updated("Remote" + tileNumber.toString())
}

//Creates a tile. Used to Create SmartGrid Tiles with A and B identities for local storage.
//Incoming URL's should be in the form <iframe name=2487A src=http://192.168.0.200/apps/api/4266/tb?access_token=98273844-2396-4c08-8c1e-d8fd61472487 style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'></iframe>
void createTile2(tileNumber, HTML1, HTML2, HTML3, HTML4, description) {
    log("createTile2", "Publishing tile: $tileNumber - $description", 1)
    def base = "Remote${tileNumber}"
    
    def tileNames = [
        "${base}-Local-A",
        "${base}-Cloud-A",
        "${base}-Local-B",
        "${base}-Cloud-B"
    ]
    def htmlContents = [HTML1, HTML2, HTML3, HTML4]
    state.tileDescriptions = state.tileDescriptions ?: [:]
    tileNames.eachWithIndex { name, i ->
        state.tileDescriptions[name.toString()] = description
        sendEvent(name: name.toString(), value: toHTML(htmlContents[i]))
        state[name.toString()] = htmlContents[i]
    }
    updated("Remote" + tileNumber.toString())
}


//Sets the description of a tile
void setTileDescription(tileNumber, description) {
	log("setTileDescription", "Set Remote description: $tileNumber - $description", 2)
	tileName1 = "Remote" + tileNumber.toString() + "-Local"
	tileName2 = "Remote" + tileNumber.toString() + "-Cloud"
    state.tileDescriptions."${tileName1}" = description
	state.tileDescriptions."${tileName2}" = description
}

// Deletes a tile
void deleteTile(tileNumber) {
    List<String> tileNames = []
    if (tileNumber < 20) {
        tileNames << "Remote${tileNumber}-Local"
        tileNames << "Remote${tileNumber}-Cloud"
    } else {
        tileNames += [
            "Remote${tileNumber}-Local",
            "Remote${tileNumber}-Cloud",
            "Remote${tileNumber}-Local-A",
            "Remote${tileNumber}-Cloud-A",
            "Remote${tileNumber}-Local-B",
            "Remote${tileNumber}-Cloud-B"
        ]
    }

    tileNames.each { name ->
        log("deleteTile", "Deleting ${name}", 0)
        device.deleteCurrentState(name)
        state.tileDescriptions.remove(name.toString())
        state.lastUpdate.remove(name.toString())
        state.remove(name.toString())
    }
}

//Make note of the last update to each tile.
void updated(tileName){    
	log("updated", "Updated tile: ${tileName}", 1)
	if (tileName.size() < 5 ) return
	now = new Date()
	state.lastUpdate."${tileName}" = now.toString()
}

//Returns a list of tiles as a list with compounded tile name, description and size.
List getTileList(){
    def tileList = []
    i = 1
    description = ""
    
    try{
    while (i <= 25){
        tileName1 = "Remote" + i + "-Local"  //We only need one as both descriptions are the same.
		myHTML = device.currentValue(tileName1)
        if (myHTML == null ) mySize = 0
        else mySize = myHTML.size()
        
        if (state.tileDescriptions."${tileName1}" != null) description = state.tileDescriptions.get(tileName1)
        else ( description = "None" )
        
        tileList.add(tileName1 + ": ${description} : (${mySize} bytes).")
		log("getTileList", "tileName is: ${tileName1.toString()} with Description: ${description}", 2)
        i++
    	}
    }
    
    catch(ex) {
        log.error("Error")
        }
        
	//log.info ("tileList is: ${tileList}")
	return tileList
}

//Returns a list of tiles as a list with compounded tile name, description and size.
List getTileListByActivity(){
    
    def tileActivityList = []
    def tileActivity = state.lastUpdate
    def temp = [:]
    def sortedActivity = [:]
    def pattern = "EEE MMM d HH:mm:ss z yyyy"
    
    tileActivity.each{ it, value ->
        lastActivity = value
        def lastActivityDate = Date.parse(pattern, lastActivity)
        //log.info ("lastActivity is: ${lastActivity}")
        temp."${it}" = lastActivityDate
    }
    sortedActivity = temp.sort{it.value}
    sortedActivity.each{ it, value ->
        description = "None"
        if ( state.tileDescriptions."${it}"  != null ) description = state?.tileDescriptions."${it}"
        tileActivityList.add(it + " @ " + value + " ($description)")    
    }
    
    //log.info ("tileActivityList is: ${tileActivityList.sort()}")
	return tileActivityList
}

//Convert [HTML] tags to <HTML> for display.
def toHTML(HTML){
	if (HTML == null) return ""
    myHTML = HTML.replace("[", "<")
    myHTML = myHTML.replace("]", ">")
    return myHTML
}

//Convert <HTML> tags to [HTML] for storage.
def unHTML(HTML){
    myHTML = HTML.replace("<", "[")
    myHTML = myHTML.replace(">", "]")
    return myHTML
}

//Procedure to run when the device is first configured.
def initialize(){
    //The device is not created instantly and calls to state may fail if made too soon.
    pauseExecution(1000)
    
    if (state.tileDescriptions == null) state.tileDescriptions = [:] 
    if (state.lastUpdate == null) state.lastUpdate = [:]  
}



//*****************************************************************************************************************************************************************************************************
//******
//****** STANDARD: Start of log()
//******
//*****************************************************************************************************************************************************************************************************

private log(name, message, int loglevel){
    
    //This is a quick way to filter out messages based on loglevel
    int threshold = settings.logging_level
    if (loglevel > threshold) {return}
   
    if ( loglevel <= 1 ) { log.info ( message)  }
    if ( loglevel >= 2 ) { log.debug ( message) }
}

//*********************************************************************************************************************************************************************
//****** End of log function
//*********************************************************************************************************************************************************************

//Functions to enhance text appearance
String bold(s) { return "<b>$s</b>" }
String italic(s) { return "<i>$s</i>" }
String underline(s) { return "<u>$s</u>" }



