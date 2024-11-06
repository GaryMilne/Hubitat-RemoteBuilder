/**
*  Remote Builder Lighting Table
*  Version: See ChangeLog
*  Download: See importUrl in definition
*  Description: Used in conjunction with child apps to generate tabular reports on device data and publishes them to a dashboard.
*
*  Copyright 2024 Gary J. Milne  
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.

*  License:
*  You are free to use this software in an un-modified form. Software cannot be modified or redistributed.
*  You may use the code for educational purposes or for use within other applications as long as they are unrelated to the 
*  production of tabular data in HTML form, unless you have the prior consent of the author.
*  You are granted a license to use Remote Builder in its standard configuration without limits.
*  Use of Remote Builder in it's Advanced requires a license key that must be issued to you by the original developer. TileBuilderApp@gmail.com
*
*  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
*  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 

*  Authors Notes:
*  For more information on Remote Builder check out these resources.
*  Original posting on Hubitat Community forum: TBD
*  Remote Builder Documentation: https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote%20Builder%20Help.pdf
*
*  Remote Builder Lighting Table - ChangeLog
*
*  Gary Milne - November 6th, 2024 @ 11:33 AM
*
**/

/* Possible Todo's
Add a global setting for transition times for colorTemp and Level.
Add an option to include selected Scenes within the table as a popup when using lights.
Add options for Small, Medium and Large controls.
Add option to select color for border that highlights whether it is CT or RGB.
Add other device types to the device filter such as Bulb\Light\Outlet\Relay
Add support for valves.
*/

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.transform.Field
import java.text.SimpleDateFormat
import java.util.Date
import java.time.LocalDate
import java.time.LocalDateTime

//These are the data for the pickers used on the child forms.
static def textScale() { return ['50', '55', '60', '65', '70', '75', '80', '85', '90', '95', '100', '105', '110', '115', '120', '125', '130', '135', '140', '145', '150', '175', '200', '250', '300', '350', '400', '450', '500'] }
static def textAlignment() { return ['Left', 'Center', 'Right', 'Justify'] }
static def opacity() { return ['1', '0.9', '0.8', '0.7', '0.6', '0.5', '0.4', '0.3', '0.2', '0.1', '0'] }
static def elementSize() { return ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10'] }

static def dateFormatsMap() { return [1: "To: yyyy-MM-dd HH:mm:ss.SSS", 2: "To: HH:mm", 3: "To: h:mm a", 4: "To: HH:mm:ss", 5: "To: h:mm:ss a", 6: "To: E HH:mm", 7: "To: E h:mm a", 8: "To: EEEE HH:mm", 9: "To: EEEE h:mm a", \
								10: "To: MM-dd HH:mm", 11: "To: MM-dd h:mm a", 12: "To: MMMM dd HH:mm", 13: "To: MMMM dd h:mm a", 14: "To: yyyy-MM-dd HH:mm", 15: "To: dd-MM-yyyy h:mm a", 16: "To: MM-dd-yyyy h:mm a", 17: "To: E @ h:mm a" ] }
static def dateFormatsList() { return dateFormatsMap().values() }

static def durationFormatsMap() { return [21: "To: Elapsed Time (dd):hh:mm:ss", 22: "To: Elapsed Time (dd):hh:mm"] }
static def durationFormatsList() { return durationFormatsMap().values() }

static def invalidAttributeStrings() { return ["N/A", "n/a", " ", "-", "--", "?", "??"] }
static def devicePropertiesList() { return ["lastOn", "lastOff", "lastOnDuration", "lastOffDuration", "roomName", "colorName", "colorMode", "power", "deviceID"] }

@Field static final codeDescription = "<b>Remote Builder - SmartGrid 1.3.1 (11/6/24)</b>"
@Field static final codeVersion = 131
@Field static final moduleName = "SmartGrid"

definition(
	    name: "Remote Builder - SmartGrid",
        description: "Generates a Grid of Objects that can be fully controlled. It can be executed from a web browser or embedded into a Hubitat Dashboard.",
        importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_SmartGrid.groovy",
        namespace: "garyjmilne", author: "Gary J. Milne", category: "Utilities", iconUrl: "", iconX2Url: "", iconX3Url: "", singleThreaded: false,
        parent: "garyjmilne:Remote Builder", 
        installOnOpen: true, oauth: true
)

//Tells the App how to direct inbound and outbound requests.
mappings {
    path("/tb") {action: [POST: "response", GET: "showApplet"] }
	path("/tb/data") { action: [POST: "toHub", GET: "fromHub"] }
}

preferences {
   page name: "mainPage"
}

def mainPage(){
	//app.updateSetting("invalidAttribute", [value: "N/A", type: "enum"])
	//app.updateSetting("defaultDateTimeFormat", 3)
	//app.updateSetting("defaultDurationFormat", 21)
	//app.updateSetting("tap", "5")
	//app.updateSetting("isLogTrace", true)
	//app.updateSetting("c2header", "Name")
	
    //Basic initialization for the initial release. If it is already initialized then compile the remote on each reload.
    if (state.initialized == null) initialize()
	else compile()
	    
    dynamicPage(name: "mainPage", title: "<div style='text-align:center;color: #c61010; font-size:30px;text-shadow: 0 0 5px #FFF, 0 0 10px #FFF, 0 0 15px #FFF, 0 0 20px #49ff18, 0 0 30px #49FF18, 0 0 40px #49FF18, 0 0 55px #49FF18, 0 0 75px #ffffff;;'> Remote Builder - " + moduleName + " 💡 </div>", uninstall: true, install: true, singleThreaded:false) {
			section(hideable: true, hidden: state.hidden.Device, title: buttonLink('btnHideDevice', getSectionTitle("Device"), 20)) {
            	
				// Input for selecting filter criteria
				input(name: "filter", type: "enum", title: bold("Filter Devices"), options: ["All Switch Devices", "All Color Temperature Devices", "All Color Devices", "All Dimmable Devices"], required: false, defaultValue: "All Switch Devices", submitOnChange: true, width: 2, newLine: true, style:"margin-right: 20px")
				// Apply switch-case logic based on the filter value
    			switch (filter) {
        			case "All Switch Devices":
						input "myLights", "capability.switch", title: "<b>Select Switch Devices</b>", multiple: true, submitOnChange: true
            			break
			        case "All Color Temperature Devices":
						input "myLights", "capability.colorTemperature", title: "<b>Select Color Temperature Devices</b>", multiple: true, submitOnChange: true
            			break
        			case "All Color Devices":
            			input "myLights", "capability.colorControl", title: "<b>Select Color Devices</b>", multiple: true, submitOnChange: true
            			break
        			case "All Dimmable Devices":
            			input "myLights", "capability.switchLevel", title: "<b>Select Dimmable Devices</b>", multiple: true, submitOnChange: true
            			break
        			default:
            			if (isLogDebug) log.debug "No filter option selected."
    			}
			}
        
            section(hideable: true, hidden: state.hidden.Endpoints, title: buttonLink('btnHideEndpoints', getSectionTitle("Endpoints"), 20)) {
                input(name: "localEndpointState", type: "enum", title: bold("Local Endpoint State"), options: ["Disabled", "Enabled"], required: false, defaultValue: "Enabled", submitOnChange: true, width: 2, style:"margin-right: 20px")
                input(name: "cloudEndpointState", type: "enum", title: bold("Cloud Endpoint State"), options: ["Disabled", "Enabled"], required: false, defaultValue: "Disabled", submitOnChange: true, width: 2, style:"margin-right: 20px")
				paragraph line (1)
				
				//Display the Endpoints with links or ask for compilation
				paragraph "<a href='${state.localEndpoint}' target=_blank><b>Local Endpoint</b></a>: ${state.localEndpoint} "
                paragraph "<a href='${state.cloudEndpoint}' target=_blank><b>Cloud Endpoint</b></a>: ${state.cloudEndpoint} "
				
				myText = "<b>Important: If these endpoints are not generated you may have to enable OAuth in the child application code for this application to work.</b><br>"
            	myText += "Both endpoints can be active at the same time and can be enabled or disable through this interface.<br>"
				myText += "Endpoints are paused if this instance of the <b>Remote Builder</b> application is paused. Endpoints are deleted if this instance of <b>Remote Builder</b> is removed.<br>"
				paragraph myText
            	paragraph line (1)
            }
        
            section(hideable: true, hidden: state.hidden.Design, title: buttonLink('btnHideDesign', getSectionTitle("Design"), 20)) {
                input(name: "displayEndpoint", type: "enum", title: bold("Endpoint to Display"), options: ["Local", "Cloud"], required: false, defaultValue: "Local", submitOnChange: true, width: 2, style:"margin-right:20px")
				input(name: "tilePreviewWidth", type: "enum", title: bold("Preview Width (x200px)"), options: [1, 2, 3, 4, 5], required: false, defaultValue: 2, submitOnChange: true, style: "width:12%")
                input(name: "tilePreviewHeight", type: "enum", title: bold("Preview Height (x190px)"), options: [1, 2, 3, 4, 5, 6], required: false, defaultValue: 2, submitOnChange: true, style: "width:12%")
				input(name: "tilePreviewBackground", type: "color", title: bold2("Preview Background Color", tb), required: false, defaultValue: "#000000", width: 3, submitOnChange: true)
				
				//Add extra space for the padding.
				def width = (tilePreviewWidth.toInteger() * 200) + 50
            	def height = (tilePreviewHeight.toInteger() * 190) + 50
				
				if ( displayEndpoint == "Local" ) paragraph "<iframe src='${state.localEndpoint}' width='${width}' height='${height}' style='border:solid; margin-left:50px; padding:25px; background-color:${tilePreviewBackground};' scrolling='no' ></iframe>"
				if ( displayEndpoint == "Cloud" ) paragraph "<iframe src='${state.cloudEndpoint}' width='${width}' height='${height}' style='border:solid; margin-left:50px; padding:25px; background-color:${tilePreviewBackground};' scrolling='no'></iframe>"
								
				paragraph line(1)
				input (name: "customizeSection", type: "enum", title: bold("Customize"), required: false, options: ["General", "Table", "Title", "Headers", "Rows", "Columns", "Info" ], defaultValue: "Table", submitOnChange: true, width: 2, newLineAfter:false, style:"margin-left:10px;margin-right:30px" )
				
				if (settings.customizeSection == "General") {
					input(name: "invalidAttribute", title: bold("Invalid Attribute String"), type: "enum", options: invalidAttributeStrings(), submitOnChange: true, defaultValue: "N/A", width: 2)
					input(name: "defaultDateTimeFormat", title: bold("Date Time Format"), type: "enum", options: dateFormatsMap(), submitOnChange: true, defaultValue: 1, width: 2)
					input(name: "defaultDurationFormat", title: bold("Duration Format"), type: "enum", options: durationFormatsMap(), submitOnChange: true, defaultValue: "(dd):hh:mm", width: 2)
				}
								
				if (settings.customizeSection == "Table") {
					input (name: "ha", type: "enum", title: bold("Horizontal Alignment"), required: false, options: ["Left", "Center", "Right" ], defaultValue: "Center", submitOnChange: true, width: 3 )
                    input (name: "va", type: "enum", title: bold("Vertical Alignment"), required: false, options: ["Top", "Center", "Bottom" ], defaultValue: "Top", submitOnChange: true, width: 3 )
					input (name: "thp", type: "enum", title: bold("Horizontal Padding"), options: elementSize(), required: false, defaultValue: "3", width: 2, submitOnChange: true)
					input (name: "tvp", type: "enum", title: bold("Vertical Padding"), options: elementSize(), required: false, defaultValue: "3", width: 2, submitOnChange: true)
				}
				
				if (settings.customizeSection == "Title") {
                	input(name: "tt", type: "string", title: bold("Title Text (? to disable)"), required: false, defaultValue: "?", width: 2, submitOnChange: true)
                    input(name: "ts", type: "enum", title: bold("Text Size %"), options: textScale(), required: false, defaultValue: "150", width: 2, submitOnChange: true)
					input(name: "tp", type: "enum", title: bold("Text Padding"), options: elementSize(), required: false, defaultValue: "5", width: 2, submitOnChange: true)
                    input(name: "ta", type: "enum", title: bold("Text Alignment"), options: textAlignment(), required: false, defaultValue: "Center", width: 2, submitOnChange: true)
                    input(name: "tc", type: "color", title: bold2("Text Color", tc), required: false, defaultValue: "#000000", width: 3, submitOnChange: true, newLine:true)
					input(name: "tb", type: "color", title: bold2("Background Color", tb), required: false, defaultValue: "#000000", width: 2, submitOnChange: true)
					input(name: "to", type: "enum", title: bold("Background Opacity"), options: opacity(), required: false, defaultValue: "1", submitOnChange: true, width:2)
				}
				
				if (settings.customizeSection == "Headers") {
					input(name: "hts", type: "enum", title: bold("Text Size %"), options: textScale(), required: false, defaultValue: "125", width: 2, submitOnChange: true)
                    input(name: "htc", type: "color", title: bold2("Text Color", htc), required: false, defaultValue: "#000000", width: 2, submitOnChange: true)
                    input(name: "hbc", type: "color", title: bold2("Background Color", hbc), required: false, defaultValue: "#90C226", width: 2, submitOnChange: true)
					input(name: "hbo", type: "enum", title: bold("Bg Opacity"), options: opacity(), required: false, defaultValue: "1", submitOnChange: true, width:2)
					input(name: "c2header", type: "string", title: bold("Column 2 Header"), required: false, defaultValue: "Name", width: 2, submitOnChange: true)
				}

				if (settings.customizeSection == "Rows") {
					input(name: "rts", type: "enum", title: bold("Text Size %"), options: textScale(), required: false, defaultValue: "100", submitOnChange: true, width:1)
                    input(name: "rtc", type: "color", title: bold2("Text Color", rtc), required: false, defaultValue: "#000000", submitOnChange: true, width:2)
                    input(name: "rbc", type: "color", title: bold2("Background Color", rbc), required: false, defaultValue: "#DDEEBB", submitOnChange: true, width:2)
					input(name: "rbo", type: "enum", title: bold("Background Opacity"), options: opacity(), required: false, defaultValue: "1", submitOnChange: true, width:2)
					input(name: "rbs", type: "color", title: bold2("Background Color - Selected Row", rbs), required: false, defaultValue: "#BFE373", submitOnChange: true, width:3)
				}
				
				if (settings.customizeSection == "Columns") {
					input(name: "hideColumn1", type: "bool", title: bold("Hide Selection Boxes?"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px")
					input(name: "hideColumn3", type: "bool", title: bold("Hide Switches?"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px")
					input(name: "hideColumn4", type: "bool", title: bold("Hide Color Control?"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px")
					input(name: "hideColumn5", type: "bool", title: bold("Hide Dimmer/Kelvin?"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px")
					input(name: "hideColumn6", type: "bool", title: bold("Hide Info Column 1"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px")
					input(name: "hideColumn7", type: "bool", title: bold("Hide Info Column 2"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px")
				}
				
				if (settings.customizeSection == "Info") {
					input(name: "hideColumn6", type: "bool", title: bold("Hide Info Column 1"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px", newLine:true)
					if (hideColumn6 == false) {
						input(name: "ih1", type: "string", title: bold("Info 1 Header Text"), required: false, defaultValue: "Info 1", width: 2, submitOnChange: true)
						input(name: "its1", type: "enum", title: bold("Size %"), options: textScale(), required: false, defaultValue: "80", width: 1, submitOnChange: true)
						input(name: "ita1", type: "enum", title: bold("Alignment"), options: textAlignment(), required: false, defaultValue: "Center", width: 2, submitOnChange: true)
						input(name: "info1Source", type: "enum", title: bold("Data Source"), required: false, multiple: false, defaultValue: "lastOn", options: devicePropertiesList(), submitOnChange: true, width: 2)
					}

					input(name: "hideColumn7", type: "bool", title: bold("Hide Info Column 2"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px", newLine:true)
					if (hideColumn7 == false) {
						input(name: "ih2", type: "string", title: bold("Info 2 Header Text"), required: false, defaultValue: "Info 1", width: 2, submitOnChange: true)
						input(name: "its2", type: "enum", title: bold("Size %"), options: textScale(), required: false, defaultValue: "80", width: 1, submitOnChange: true)
						input(name: "ita2", type: "enum", title: bold("Alignment"), options: textAlignment(), required: false, defaultValue: "Center", width: 2, submitOnChange: true)
						input(name: "info2Source", type: "enum", title: bold("Data Source"), required: false, multiple: false, defaultValue: "lastOnDuration", options: devicePropertiesList(), submitOnChange: true, width: 2)
					}
				}
				
				paragraph line(2)
				paragraph "<b>Important:</b> This is a live remote. Pressing any of the buttons or controls will execute the associated actions.<br>"
            }
				
        //Start of Publish Section
		section(hideable: true, hidden: state.hidden.Publish, title: buttonLink('btnHidePublish', getSectionTitle("Publish"), 20)) {
            input(name: "myRemote", title: bold("Attribute to store the Remote? (Optional)"), type: "enum", options: parent.allTileList(), required: false, submitOnChange: true, width: 3, defaultValue: 0, newLine: false)
            input(name: "myRemoteName", type: "text", title: bold("Name this Remote"), submitOnChange: true, width: 3, defaultValue: "New Remote", newLine: false, required: true)
            input(name: "tilesAlreadyInUse", type: "enum", title: bold("For Reference Only: Remotes in Use"), options: parent.getTileList(), required: false, defaultValue: "Remotes List", submitOnChange: true, width: 3)
			input(name: "eventTimeout", type: "enum", title: bold("Event Timeout (millis)"), required: false, multiple: false, defaultValue: "2000", options: ["0", "250", "500", "1000", "1500", "2000", "5000", "10000", "Never"], submitOnChange: true, width: 2)
                                    
            if (myRemoteName) app.updateLabel(myRemoteName)
            myText =  "Publishing a remote is optional and only required if it will be used within a Hubitat dashboard. Remotes can be accessed directly via the URL's in the Endpoints section and bypass the Dashboard entirely if desired. "
			myText += "The <b>Event Timeout</b> period is how long Tile Builder will wait for subsequent events before publishing the table. Re-publishing a table will cause it to refresh on the dashboard. This setting batches multiple changes into a single publishing event. "
            myText += "Lowering the event timeout will make the table more responsive but also increase the number of refreshes. If the eventTimeout is set to 'Never' then you must manually click the refresh Icon to synchronise the table.<br>"
            
			myText += "If publishing to a dashboard is enabled then the The <b>Remote Name</b> given here will also be used as the name for this instance of Remote Builder. "
			myText += "Appending the name with your chosen remote number can make parent display more readable. Only <b>links</b> to the Local and Cloud Endpoints are stored in the Remote Builder Storage Device when publishing is enabled.<br>"
            paragraph myText
            paragraph line(1)
            
			if ( state.compiledLocal != null  && state.compiledCloud && settings.myRemote != null && myRemoteName != null) {
                input(name: "publishSubscribe", type: "button", title: "Publish and Subscribe", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2)
                input(name: "unsubscribe", type: "button", title: "Delete Subscription", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2)
            } else input(name: "cannotPublish", type: "button", title: "Publish", backgroundColor: "#D3D3D3", textColor: "black", submitOnChange: true, width: 2)			
        }
        //End of Publish Section
		
		//Start of More Section
        section {
			paragraph line(2)
            input(name: "isMore", type: "bool", title: dodgerBlue("<b>More Options</b>"), required: false, multiple: false, defaultValue: false, submitOnChange: true, width: 2)
            if (isMore == true) {
                //Horizontal Line
                paragraph "In this section you can enable logging for various aspects of the program. This is usually used for debugging purposes and by default all logging other than errors is turned off by default. You can also rebuild the endpoints if you refresh the Oauth client secret."				
				input(name: "isLogConnections", type: "bool", title: "<b>Record All Connection Requests?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogActions", type: "bool", title: "<b>Record All Action Requests?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogPublish", type: "bool", title: "<b>Enable Publishing logging?</b>", defaultValue: false, submitOnChange: true, width: 3)
                input(name: "isLogDeviceInfo", type: "bool", title: "<b>Enable Device Details logging?</b>", defaultValue: false, submitOnChange: true, width: 3)
                input(name: "isLogError", type: "bool", title: "<b>Log errors encountered?</b>", defaultValue: true, submitOnChange: true, width: 3, newLine: true)
				input(name: "isLogDebug", type: "bool", title: "<b>Enable Debug logging?</b>", defaultValue: false, submitOnChange: true, width: 3)
                input(name: "isLogTrace", type: "bool", title: "<b>Enable Trace logging?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "rebuildEndpoints", type: "button", title: "Rebuild Endpoints", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 12)
            }
			paragraph line(2)

            //Now add a footer.
            myDocURL = "<a href='https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote%20Builder%20Help.pdf' target=_blank> <i><b>Remote Builder Help</b></i></a>"
            myText = '<div style="display: flex; justify-content: space-between;">'
            myText += '<div style="text-align:left;font-weight:small;font-size:12px"> <b>Documentation:</b> ' + myDocURL + '</div>'
            myText += '<div style="text-align:center;font-weight:small;font-size:12px">Version: ' + codeDescription + '</div>'
            myText += '<div style="text-align:right;font-weight:small;font-size:12px">Copyright 2022 - 2024</div>'
            myText += '</div>'
            paragraph myText
        }
        //End of More Section
    }
}


// Gets the state of the various lights that are being tracked and puts them into a JSON format for inclusion with the script 
def getJSON() {
    if (isLogTrace) log.trace("<b>Entering: GetJSON</b>")
    
    // List to hold device attribute data
    def deviceAttributesList = []
	def eventData = [:]
	
    
    // Iterate through each device
    myLights.each { device ->
        // Use LinkedHashMap to maintain the order of fields
        def deviceData = new LinkedHashMap()
        
        // Ensure 'name' and 'deviceID' are added first in the correct order
        deviceData.put("name", device.displayName)
        deviceData.put("deviceID", device.getId())
        deviceData.put("switch", device.currentValue("switch"))
        deviceData.put("type", 1)

        // Check if the device has "SwitchLevel" capability
        if (device.hasCapability("SwitchLevel")) {
            deviceData.put("level", (device.currentValue("level")?.toInteger() ?: 100))
            deviceData.put("type", 2)
        }

		//If the device has ColorTemperature but NOT ColorControl then it is a CT only device.
        if ( device.hasCapability("ColorTemperature") && !device.hasCapability("ColorControl") ) {
			deviceData.put("type", 3)
			deviceData.put("colorTemperature", (device.currentValue("colorTemperature")?.toInteger() ?: 2000))
        }

		//If the device has ColorControl but NOT ColorTemperature then it is an RGB device.
		if (device.hasCapability("ColorControl") && !device.hasCapability("ColorTemperature") ) {
			deviceData.put("type", 4)
			def hsvMap = [hue: device.currentValue("hue") ?: 100, saturation: device.currentValue("saturation") ?: 100, value: device.currentValue("level")?.toInteger() ?: 100 ]
			def color = getHEXfromHSV(hsvMap)	
			deviceData.put("color", color)
		}
		
		//If the device has ColorControl AND ColorTemperature then it is a RGBW device.
        if ( device.hasCapability("ColorControl") && device.hasCapability("ColorTemperature") ) {
			deviceData.put("type", 5)
			def hsvMap = [hue: device.currentValue("hue") ?: 100, saturation: device.currentValue("saturation") ?: 100, value: device.currentValue("level")?.toInteger() ?: 100 ]
			def color = getHEXfromHSV(hsvMap)	
			deviceData.put("color", color)
			deviceData.put("colorTemperature", (device.currentValue("colorTemperature")?.toInteger() ?: 2000))
			deviceData.put("colorMode", device.currentValue("colorMode"))
        }
		
		//Gather event information if needed for either of the two columns.
		if (hideColumn6 == false || hideColumn7 == false) {
			deviceDetails = getDeviceInfo(device)
			//log.info ("eventData is: $eventData")
			deviceData.put("info1", deviceDetails."${info1Source}")
			deviceData.put("info2", deviceDetails."${info2Source}")
		}
		
        // Add device data to the list
        deviceAttributesList << deviceData
		//log.info("Device: $deviceData.name is type: $deviceData.type and data is: $deviceData")
    }
    
    // Convert the list of device attributes to JSON format
    def jsonOutput = JsonOutput.toJson(deviceAttributesList)

    // Pretty print the JSON output
    def updatedJson = JsonOutput.prettyPrint(jsonOutput)
    
	if (isLogDebug) log.debug("getJSON Output: $updatedJson")
    state.JSON = updatedJson
}


//Gets information about the lastOn, lastOff etc and and put it into a map. Data from selected fields will be mapped into the Info columns.
def getDeviceInfo(device){
	def lastOn
	def lastOnDuration
	def lastOnInstant
	def lastOff
	def lastOffDuration
	def lastOffInstant
	
	def deviceID = device?.getId()
	def roomName = device?.getRoomName()
	def colorName = device?.currentValue("colorName")
	def colorMode = device?.currentValue("colorMode")
	def power = device?.currentValue("power")
	//log.info ("power is $power")

	try {
		//Looks through a large number of entries that match the right criteria and only return the first one.  This allows the use of a larger number of events to be queried with low cost.
		def lastOnEvent = device.events(max: 500) .findAll { it.name == "switch" && it?.value == "on" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
		def lastOffEvent = device.events(max: 500) .findAll { it.name == "switch" && it?.value == "off" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
		
		if (lastOnEvent != null) {
			lastOn = formatTime(lastOnEvent?.getDate(), defaultDateTimeFormat.toInteger() ?: 3)
			lastOnDuration = formatTime(lastOnEvent?.getDate(), defaultDurationFormat.toInteger()  ?: 21 )
			lastOnInstant = formatTime(lastOnEvent?.getDate(), 0 )
		}
		if (lastOffEvent != null) {
			lastOff = formatTime(lastOffEvent?.getDate(), defaultDateTimeFormat.toInteger() ?: 3)
			lastOffDuration = formatTime(lastOffEvent?.getDate(), defaultDurationFormat.toInteger() ?: 21 )
			lastOffInstant = formatTime(lastOffEvent?.getDate(), 0 )
		}	
		
		if (lastOff != null && lastOn != null) {
		def durations = getDuration(lastOnInstant, lastOffInstant)
		lastOnDuration = durations.lastOnDuration
		lastOffDuration = durations.lastOffDuration
		}
	}
	
	catch (Exception exception) { log.error ("Exception is: $exception") }
	
	def result = [lastOff: lastOff, lastOffInstant: lastOffInstant, lastOn: lastOn, lastOnInstant: lastOnInstant, lastOnDuration: lastOnDuration, lastOffDuration: lastOffDuration, roomName: roomName, colorName: colorName, colorMode: colorMode, power: power, deviceID: deviceID ].collectEntries { key, value -> [key, value != null ? value : invalidAttribute.toString()] }
	
	//log.info("Returning map: $result")
    return result
	
}


//*******************************************************************************************************************************************************************************************
//**************
//**************  Time and Date Related Functions
//**************
//*******************************************************************************************************************************************************************************************

//This is derived from the formatTime function used in Grid but in this case the only Time format received is from Events which are always "java.sql.Timestamp".
//Receives a time as an event timestamp and converts it into one of many alternate time formats.
def formatTime(timeValue, int format) {
	def isLogDateTime = false
	if (isLogDateTime) log.info("<b>formatTime: Time received is: $timeValue and requesting format: $format</b>")
    def myLongTime
    
    // N/A means the requested attribute was not found.
    if (timeValue == "N/A") return 0
	
	myLongTime = timeValue.getTime()
    if (isLogDateTime) log.info("<b>Received timestamp: $timeValue  -  Converted to: $myLongTime")

    Date myDateTime = new Date(myLongTime)
    if (myDateTime == null || myDateTime == "null") return "N/A"

    if (format == 0) return myLongTime
    if (format == 1) targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    //Return the selected DateTime format
    if (2 <= format && format <= 20) targetFormat = new SimpleDateFormat(getDateTimeFormatDescription(format))

    //This is the elapsed time calculation including seconds
    if (format == 21 || format == 22) {
        // Get the number of seconds this event occurred after the epoch
        def diff = ((now() - myLongTime) / 1000).toLong()
        if (format == 21) elapsedTime = convertSecondsToDHMS(diff, true)
        if (format == 22) elapsedTime = convertSecondsToDHMS(diff, false)
        if (isLogDateTime && isLogDetails) log.info("Elapsedtime is $elapsedTime")
        return elapsedTime
    }
	
	    //This is the remaining time calculation including seconds
    if (format == 23 || format == 24) {
        // Get the number of seconds this event occurred after the epoch
        def diff = ((myLongTime - now()) / 1000).toLong()
        if (format == 23) remainingTime = convertSecondsToDHMS(diff, true)
        if (format == 24) remainingTime = convertSecondsToDHMS(diff, false)
        if (isLogDateTime && isLogDetails) log.info("Remaining time is $remainingTime")
        return remainingTime
    }

    //Depending on the mode the date may already have been converted to a string using a cleanup.  If that is the case a second conversion will fail so we will just return the original converted value.
    try {
        Date date = new Date(myLongTime)
        String formattedDateTime = targetFormat.format(date)
        if (isLogDateTime && isLogDetails) log.info("formatTime: Returning date $formattedDateTime")
        return formattedDateTime
    }
    catch (Exception ignored) {
        return timeValue
    }
}

//Get the Index that corresponds to the Text Description that is used by formatTime() to get the right format.
static def getDateTimeFormatIndex(description) {
    def map = dateFormatsMap()
    for (entry in map) {
        if (entry.value == description) return entry.key
    }
    return 0  // If description not found
}

//Get the Index that corresponds to the Text Description that is used by formatTime() to get the right format.
static def getDurationFormatIndex(description) {
    def map = durationFormatsMap()
    for (entry in map) {
        if (entry.value == description) return entry.key
    }
    return 0  // If description not found
}

//Get the Date Time text description that corresponds to an index.
static def getDateTimeFormatDescription(format) {
    def dateFormatString = dateFormatsMap()[format]
    def dateFormat = dateFormatString.replaceAll('To: ', '')
    //log.info ("The requested format is: $dateFormat")
    return dateFormat
}

//Calculates the elapsed time since an event and returns the string value. Return value omits days if the value is 0.
String convertSecondsToDHMS(long seconds, boolean includeSeconds) {
    if (isLogDateTime) log.info("<convertSecondsToDHMS: Received: $seconds")
    def days = (seconds / (24 * 3600)) as int
    def hours = ((seconds % (24 * 3600)) / 3600) as int
    def minutes = ((seconds % 3600) / 60) as int
    def remainingSeconds = (seconds % 60) as int

    // Check if days are greater than 0
    def daysString = days > 0 ? "${days}d " : ""
    def hoursString = hours > 0 ? "${hours}h " : ""

    if (!includeSeconds) return "${daysString}${hoursString} ${minutes}m"
    else return "${daysString}${hoursString}${minutes}m ${remainingSeconds}s"
}

//Gets the duration between two instants (lastOn / lastOff) that would typically be an on/off pairing.
def getDuration(long lastOnEvent, long lastOffEvent) {
	if (isLogTrace) log.trace("<b>getDuration: Received $lastOnEvent, $lastOffEvent</b>")
    def includeSeconds = false
    long diff = lastOnEvent - lastOffEvent
	
	if (defaultDurationFormat.toInteger() == 21) includeSeconds = true
	else includeSeconds = false
	
	//Invalid
    if (diff == 0) return [currentRunTime: invalidAttribute.toString(), lastRunTime: invalidAttribute.toString() ]  //If there is no difference then the duration is not valid.
	
	//LastOn is more recent than LastOff so the device must be on and still running. So diff is equal to the amount of current runtime.
    if (diff > 0) {
        diff = (now() - lastOnEvent) / 1000
		lastOnDuration = "<span style='color: green;'>" + convertSecondsToDHMS(diff, includeSeconds).toString() + "</span>"
		//log.info ("Device is ON and current runtime is: $lastOnDuration")
		
		diff = (lastOnEvent - lastOffEvent) / 1000
		lastOffDuration = convertSecondsToDHMS(diff, includeSeconds).toString()
		//log.info ("Device is ON and last off duration is: $lastOffDuration")
    }  
	
	//LastOff is more recent than LastOn so the device must be off. So diff is equal to the amount of the last runtime (from lastOn -> last Off) 
    if (diff <= 0) {
        diff = (now() - lastOffEvent) / 1000
		lastOffDuration = "<span style='color: red;'>" + convertSecondsToDHMS(diff, includeSeconds).toString() + "</span>"
		//log.info ("Device is OFF and lastOffDuration is: $lastOffDuration")
		
		diff = (lastOffEvent - lastOnEvent) / 1000
		lastOnDuration = "<span style='color: red;'>" + convertSecondsToDHMS(diff, includeSeconds).toString() + "</span>"
		//log.info ("Device is OFF and lastOnDuration is: $lastOnDuration")
		
    }  
	result = [lastOnDuration: lastOnDuration, lastOffDuration: lastOffDuration ]
	//log.info ("Result is: $result")
	return result
}

//*******************************************************************************************************************************************************************************************
//**************
//**************  Compile Functions
//**************
//*******************************************************************************************************************************************************************************************

//Compress the fixed components text output and generate the version that will be used by the browser.
def compile(){
	//log.info ("Running Compile")
	if (isLogTrace) log.trace("<b>Entering: Compile</b>")
		
	def html1 = myHTML()
	def content = condense(html1)
	
	//Table Horizontal Alignment
	if (ha == "Left") content = content.replace('#ha#', "flex-start" )
	if (ha == "Center") content = content.replace('#ha#', "center" )
	if (ha == "Right") content = content.replace('#ha#', "flex-end" )
	
	//Table Vertical Alignment
	if (va == "Top") content = content.replace('#va#', "flex-start" )
	if (va == "Center") content = content.replace('#va#', "center" )
	if (va == "Bottom") content = content.replace('#va#', "flex-end" )
	
	//Table Padding
	content = content.replace('#thp#', thp )
	content = content.replace('#tvp#', tvp )

	//Info Columns
	content = content.replace('#Info1#', toHTML(ih1) )	// Info 1 Header Text
	content = content.replace('#its1#', its1 )	// Info 1 Text Size
	content = content.replace('#ita1#', ita1 )	// Info 1 Text Alignment
	
	content = content.replace('#Info2#', toHTML(ih2) )	// Info 2 Header Text
	content = content.replace('#its2#', its2 )	// Info 2 Text Size
	content = content.replace('#ita2#', ita2 )	// Info 2 Text Alignment
	
	content = content.replace('#c2header#', c2header )	// Info 2 Text Alignment
	
	
	if (tt == "?") { content = content.replace('#titleDisplay#', 'none' ) }
	else {
		content = content.replace('#titleDisplay#', 'block' )	// Display Title or not
		content = content.replace('#tt#', toHTML(tt) )	// Title Text
		content = content.replace('#ts#', ts )	// Title Size
		content = content.replace('#tp#', tp )	// Title Padding
		content = content.replace('#ta#', ta )	// Title Alignment
		content = content.replace('#tc#', tc )	// Title Color
		def mytb = convertToHex8(tb, to.toFloat())  //Calculate the new color including the opacity.
		content = content.replace('#tb#', mytb )	// Title Background Color// Display Title or not
	}
	
	content = content.replace('#hts#', hts )	// Header Text Size
	content = content.replace('#htc#', htc )	// Header Text Color
	
	def myhbc = convertToHex8(hbc, hbo.toFloat())  //Calculate the new color including the opacity.
	content = content.replace('#hbc#', myhbc )	// Header Background Color

	content = content.replace('#rts#', rts )	// Row Text Size
	content = content.replace('#rtc#', rtc )	// Row Text Color
	
	def myrbc = convertToHex8(rbc, rbo.toFloat())  //Calculate the new color including the opacity.
	content = content.replace('#rbc#', myrbc )	// Row Background Color
	content = content.replace('#rbs#', rbs )	// Row Background Color Selected
	
	//Hide unwanted columns
	content = content.replace('#hideColumn1#', hideColumn1 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn3#', hideColumn3 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn4#', hideColumn4 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn5#', hideColumn5 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn6#', hideColumn6 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn7#', hideColumn7 ? 'none' : 'table-cell')
		
	content = content.replace('#BrowserTitle#', myRemoteName)
		
	def localContent = content.replace("#URL#", state.localEndpointData )
	def cloudContent = content.replace("#URL#", state.cloudEndpointData )
	
	// Saves a copy of this finalized HTML\CSS\SVG\SCRIPT so that it does not have to be re-calculated. Everything else is done via the loading of JSON data.
    state.compiledLocal = localContent
    state.compiledCloud = cloudContent
    state.compiledSize = state.compiledLocal.size()
	
	def now = new Date()
	state.compiledDataTime = now.format("EEEE, MMMM d, yyyy '@' h:mm a")
}

//Remove any wasted space from the compiled version to shorten load times.
def condense(String input) {
	if (isLogTrace) log.trace("<b>Entering: Condense</b>")
	def initialSize = input.size()
    if (isLogDebug) log.debug ("Condense: Original Size: " + initialSize )
	
	//Reduce any groups of 2 or more spaces to a single space
	input = input.replaceAll(/ {2,}/, " ")
	if (isLogDebug) log.debug ("Condense: After concurrent spaces removed: " + input.size() + " bytes." )
	
	//Remove any Tabs from the file
	input = input.replaceAll(/\t/, "")
	if (isLogDebug) log.debug ("Condense: After concurrent tabs removed: " + input.size() + " bytes." )
	
	//Replace "; " with ";"
	input = input.replaceAll("; ", ";")
	input = input.replaceAll(": ", ":")
	input = input.replaceAll("> <", "><" )
	input = input.replaceAll(" = ", "=" )
	
	//Remove any leading spaces on a line
	input = input.replaceAll(/(?m)^\s+/, "")
	
    //Replace any comments in the HTML\CSS\SVG section that will be between <!-- and -->
    input = input.replaceAll(/<!--.*?-->/, "")
    if (isLogDebug) log.debug ("Condense: After HTML\\CSS\\SVG comments removed: " + input.size() + " bytes." )
	
	input = input.replaceAll(/\/\/[^\n\r]*/, "")
	if (isLogDebug) log.debug ("Condense: After all comments with leading \\\\ are removed: " + input.size() + " bytes." )
    
    //Replace any comments in the SCRIPT section that will be between \* and *\  Note: Comments beginning with \\ will not be removed.
    input = input.replaceAll(/(?s)\/\*.*?\*\//, "")
	
    if (isLogDebug) log.debug  ("Condense: After SCRIPT comments removed: " + input.size() + " bytes." )
    if (isLogDebug) log.debug ("Condense: Before: " + String.format("%,d", initialSize) + " - After: " + String.format("%,d", input.size()) + " bytes.")
	
    return input 
}

//Takes a JSON list of devices and changes and implements them.
def applyChangesToDevices(changes) {
	if (isLogTrace) log.trace("<b>Entering: applyChangesToDevices</b>")
	if (isLogDeviceInfo) log.debug ("Changes are: $changes")
    changes.each { change ->
        def device = findDeviceById(change.deviceID)
        if (device) {
            change.changes.each { key, values ->
                def newValue = values[1]  // Get the new value for each property
                switch (key) {
                    case 'switch':
                        if (newValue == "on") { device.on() }
						else { device.off() }
                        break
                    case 'level':
                        //device.setLevel(newValue)
						device.setLevel(newValue, 0.4)
                        break
					case 'name':
                        device.setLabel(newValue)
                        break
                    case 'colorTemperature':
						device.setColorTemperature(newValue, null, 0.4)
                        break
					case 'color':
						//Convert hex color (#FF8E43) to R,G,B
						def RGB = hubitat.helper.ColorUtils.hexToRGB("${newValue}")
						//Convert R,G,B to HSV array
    					def HSV = hubitat.helper.ColorUtils.rgbToHSV(RGB)
                		def hsvMap = [hue: HSV[0], saturation: HSV[1], level: HSV[2]]
						device.setColor(hsvMap)
						break
                    default:
                        if (isLogDebug) log.warn "Unhandled change: $key"
						break
                }
            }
        }
    }
}


//*******************************************************************************************************************************************************************************************
//**************
//**************  End of Compile Functions
//**************
//*******************************************************************************************************************************************************************************************


//*******************************************************************************************************************************************************************************************
//**************
//**************  Endpoint Activity Handling
//**************
//*******************************************************************************************************************************************************************************************

//This delivers the applet content.
def showApplet() {
	if (isLogTrace) log.trace("<b>Entering: showApplet</b>")
    def isLocal = false
    def isCloud = false
    
	//Make sure the
	def host = request.headers?.Host?.first()
	def protocol = request.headers?.'X-hubitat-scheme'?.first()
	
    if(request.requestSource == "local") { isLocal = true ; if (isLogConnections) log.info("Connection: $request.requestSource with protocol $protocol and host $host.") }
    if(request.requestSource == "cloud") { isCloud = true ; if (isLogConnections) log.info("Connection: $request.requestSource and host $host.") }
    
    if ( localEndpointState == "Disabled" && isLocal == true) {
        result = render contentType: "text/html;charset=UTF-8", data:disabledEndpointHTML(), status:200
        return result
    }
    
    if ( cloudEndpointState == "Disabled" && isCloud == true) {
        result = render contentType: "text/html;charset=UTF-8", data:disabledEndpointHTML(), status:200
        return result
    }
        
    //If it gets this far the interface must be enabled.
    if( isLocal ) { result = render contentType: "text/html;charset=UTF-8", data:state.compiledLocal, status:200  }
    if( isCloud ) { result = render contentType: "text/html;charset=UTF-8", data:state.compiledCloud, status:200  }
    return result
}

//Deliver and Endpoint Disabled Message
def disabledEndpointHTML(){
	if (isLogTrace) log.trace("<b>Entering: disabledEndpointHTML</b>")
    myHTML = '''<body style="display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; font-family: Arial, sans-serif; background-color: #f0f0f0;">
    <div style="text-align: center; font-size: 24px; line-height: 1.5;color:red;">Endpoint<br>Disabled</div></body>'''
    return myHTML
}

// Allows the client to send the changed Device List and JSON to the Hub. Bulb types are 1: switch, 2: switchLevel (dimmer), 3: colorTemperature, 4: colorControl - HSL, 5: colorControl - HSV
def toHub() {
	if (isLogTrace) log.trace("<b>Entering: toHub</b>")
    // Extract the body field
    def bodyJson = request.body
    if (isLogDeviceInfo) log.info ("<b>Uploading device data via toHub():</b> ${bodyJson}")
	
	//Get the latest JSON for the most up to date data
	getJSON()

    // Parse JSON
    def slurper = new JsonSlurper()
    def group1 = slurper.parseText(state.JSON)  // Original state
    def group2 = slurper.parseText(bodyJson)   // New state
	
    if (isLogDebug) log.debug ("state.JSON is: $group1")
    if (isLogDebug) log.debug ("Device Data is: $group2")
	
    // Map the second group by 'deviceID' for easier comparison
    def group2Map = group2.collectEntries { [(it.deviceID): it] }
    
    // Find changes
    def changes = []

    // Compare devices using deviceID as the constant
    group1.each { item1 ->
        def item2 = group2Map[item1.deviceID]
        if (item2) {  // If a matching deviceID is found
            def diff = [:]
            
            // Compare each key except for the deviceID and type (which are constants)
            item1.each { key, value ->
                // Only compare if the key exists in both group1 and group2
                if (item2.containsKey(key) && key != "deviceID" && key != "type") {
                    if (key == "colorTemperature") {
                        // Calculate the percentage difference
                        def oldTemp = item1.colorTemperature ?: 0
                        def newTemp = item2.colorTemperature ?: 0
                        def tempDifference = Math.abs(oldTemp - newTemp)
                        // Only consider it a change if the difference is greater than 50 Kelvin. Sometimes the values on the returned controls vary by 1-5 Kelvin for no known reason.
                        if (tempDifference > 50) {
                            diff[key] = [oldTemp, newTemp]
                        }
                    } else if (item2[key] != value) {
                        // For all other keys, check if there's a difference
                        diff[key] = [item1[key], item2[key]]  // Capture both old and new values
                    }
                }
            }	
					
            // If there are differences, add them to the changes array
            if (!diff.isEmpty()) {
                changes << [name: item2.name, deviceID: item1.deviceID, changes: diff]  // Include deviceID and changes
            }
        }
    }

	// Print changes for each device one at a time
	changes.each { change ->
    	def key = change.changes.keySet().first()  // Get the key of the change (since there's only one)
    	def values = change.changes[key]           // Get the old and new values
    	if (isLogActions) log.info dodgerBlue("<b>Action: ${change.name} (ID: ${change.deviceID}): $key: ${values[0]} ---> ${values[1]} </b>")
	}

    // Apply all the changes to the devices.
    applyChangesToDevices(changes)
    result = render contentType: "application/json", data: "OK", status: 200
    return result
}

//Allows the client to get the Device List and JSON info from the Hub
def fromHub(){
	if (isLogTrace) log.trace ("<b>Entering fromHUB</b>")
	getJSON()
	
	if (isLogDeviceInfo) log.info("<b>Downloading device data via fromHub():</b> $state.JSON")
	result = render contentType: "application/json", data: state.JSON, status: 200
	return result
}

//*******************************************************************************************************************************************************************************************
//**************
//**************  End of Endpoint Activity Handling
//**************
//*******************************************************************************************************************************************************************************************


 

//*******************************************************************************************************************************************************************************************
//**************
//**************  Screen UI and Management Functions
//**************
//*******************************************************************************************************************************************************************************************
//This is the standard button handler that receives the click of any button control.
def appButtonHandler(btn) {
	if (isLogTrace) log.trace("<b>Entering: appButtonHandler: Clicked on button: $btn</b>")
    
	switch (btn) {
		case "rebuildEndpoints":
			createAccessToken()
            state.localEndpoint = "${getFullLocalApiServerUrl()}/tb?access_token=${state.accessToken}"
            state.cloudEndpoint = "${getFullApiServerUrl()}/tb?access_token=${state.accessToken}"
			state.localEndpointData = "${getFullLocalApiServerUrl()}/tb/data?access_token=${state.accessToken}"
            state.cloudEndpointData = "${getFullApiServerUrl()}/tb/data?access_token=${state.accessToken}"
			if (isLogDebug) log.debug ("Endpoints have been rebuilt")
			compile()
            break
		case "Compile":
            compile()
            break
        case "btnHideDevice":
            state.hidden.Device = state.hidden.Device ? false : true
            break
        case "btnHideEndpoints":
            state.hidden.Endpoints = state.hidden.Endpoints ? false : true
            break
        case "btnHideDesign":
            state.hidden.Design = state.hidden.Design ? false : true
            break
        case "btnHidePublish":
            state.hidden.Publish = state.hidden.Publish ? false : true
            break
		case "publishSubscribe":
            publishSubscribe()
            break
        case "unsubscribe":
            deleteSubscription()
            break
    }
}

//Returns a formatted title for a section header based on whether the section is visible or not.
def getSectionTitle(section) {
	if (section == "Introduction") {
        if (state.hidden.Intro == true) return sectionTitle("Introduction ▶") else return sectionTitle("Introduction ▼")
    }
	if (section == "Device") {
        if (state.hidden.Device == true) return sectionTitle("Device ▶") else return sectionTitle("Device ▼")
    }
    if (section == "Endpoints") {
        if (state.hidden.Endpoints == true) return sectionTitle("Endpoints ▶") else return sectionTitle("Endpoints ▼")
    }
    if (section == "Design") {
        if (state.hidden.Design == true) return sectionTitle("Design Table ▶") else return sectionTitle("Design Table ▼")
    }
    if (section == "Publish") {
        if (state.hidden.Publish == true) return sectionTitle("Publish Remote ▶") else return sectionTitle("Publish Remote ▼")
    }
}

String buttonLink(String btnName, String linkText, int buttonNumber) {
    //if (isLogTrace) log.trace("<b>buttonLink: Entering with $btnName  $linkText  $buttonNumber</b>")
    def myColor, myText
    Integer myFont = 16

    if (buttonNumber == settings.activeButton) myColor = "#00FF00" else myColor = "#000000"
    if (buttonNumber == settings.activeButton) myText = "<b><u>${linkText}</u></b>" else myText = "<b>${linkText}</b>"

    return "<div class='form-group'><input type='hidden' name='${btnName}.type' value='button'></div><div><div class='submitOnChange' onclick='buttonClick(this)' style='color:${myColor};cursor:pointer;font-size:${myFont}px'>${myText}</div></div><input type='hidden' name='settings[$btnName]' value=''>"
}

//*******************************************************************************************************************************************************************************************
//**************
//**************  End Screen UI and Management Functions
//**************
//*******************************************************************************************************************************************************************************************



//*******************************************************************************************************************************************************************************************
//**************
//**************  Publishing Functions
//**************
//*******************************************************************************************************************************************************************************************

//This function removes all existing subscriptions for this app and replaces them with new ones corresponding to the devices and attributes being monitored.
void publishSubscribe() {
    if (isLogTrace) log.trace("<b>Entering: publishSubscribe</b>")
	if (isLogPublish) log.info("<b>Creating subscriptions for Tile: $myRemote with description: $myRemoteName.</b>")
	
    //Remove all existing subscriptions
    unsubscribe()
    
	// List of attributes you want to subscribe to
	def attributesToSubscribe = ["switch", "hue", "saturation", "level", "colorTemperature"]
	deleteSubscription()
	
	// Configure subscriptions
	myLights.each { device ->
    	attributesToSubscribe.each { attribute ->
        	// Check if the device supports the attribute before subscribing
        	if (device.hasAttribute(attribute)) {
            	subscribe(device, attribute, handler)
        	} 
    	}
	}

    //Now we call the publishRemote routine to push the new information to the device attribute.
    publishRemote()
}

//Save the current HTML to the variable. This is the function that is called by the scheduler.
void publishRemote(){
	if (isLogTrace) log.trace("<b>Entering: publishRemote</b>")
    
    //Test whether we can create a cloud Endpoint to see if OAuth is enabled.
    try {
		if( !state.accessToken ) createAccessToken()
        state.cloudEndpoint = getFullApiServerUrl() + "/tb?access_token=" + state.accessToken
        }
	catch (Exception e){
        if (isLogError) log.error("This app is not OAuth Enabled.  Go to: <b>Developer Tools</b> / <b>Apps Code</b> and open the code for this app.  Click on <b>OAuth</b> and then <b>Enable OAuth in App</b> and leave it athe default values.")
        }
    
    if (isLogPublish) log.info("publishTable: Remote $myRemote ($myRemoteName) is being refreshed.")
    
    myStorageDevice = parent.getStorageDevice()
    if ( myStorageDevice == null ) {
        if (isLogError) log.error("publishTable: myStorageDevice is null. Is the device created and available? This error can occur immediately upon hub startup. Nothing published.")
        return
    }
    
	def tileLink1 = "[!--Generated:" + now() + "--][div style='height:100%; width:100%; scrolling:no; overflow:hidden;'][iframe src=" + state.localEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe][div]"
	def tileLink2 = "[!--Generated:" + now() + "--][div style='height:100%; width:100%; scrolling:no; overflow:hidden;'][iframe src=" + state.cloudEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe][div]"
		
	if (isLogPublish) log.info ("publishRemote: tileLink1 is: $tileLink1")
		
    myStorageDevice.createTile(settings.myRemote, tileLink1, tileLink2, settings.myRemoteName)
}

//This should get executed whenever any of the subscribed devices receive an update to the monitored attribute. Delays will occur if the eventTimeout is > 0
def handler(evt) {
	if (isLogTrace) log.trace("<b>Entering: handler with $evt</b>")
    //Handles the initialization of new variables added after the original release.
    //if (state.variablesVersion == null || state.variablesVersion < codeVersion()) updateVariables()
    
    if (isLogPublish) log.info("<b>handler: Event received from Device:${evt.device}  -  Attribute:${evt.name}  -  Value:${evt.value}</b>")

	if (eventTimeout.toString() == "Never" ) {
		if (isLogPublish) log.info("handler: Event processing is disabled because event timeout is set to 'Never'.")
	}
	else {
    	//Publish slightly in the future to allow multiple events to be batched together.
    	runInMillis(eventTimeout.toInteger(), publishRemote, [overwrite: true])
		if (isLogPublish) log.info("handler: publishRemote called to run in: ${eventTimeout.toInteger()} milliseconds.")
	}
}

//Deletes all event subscriptions.
void deleteSubscription() {
    if (isLogTrace) log.trace("<b>Entering: deleteSubscription</b>")
    if (isLogPublish) log.info("deleteSubscription: Deleted all subscriptions. To verify click on the App ⚙️ Symbol and look for the Event Subscriptions section.")
    unsubscribe()
}

//*******************************************************************************************************************************************************************************************
//**************
//**************  End of Publishing Functions
//**************
//*******************************************************************************************************************************************************************************************


//*******************************************************************************************************************************************************************************************
//**************
//**************  Utility Functions
//**************
//*******************************************************************************************************************************************************************************************

//Returns a string containing the var if it is not null. Used for the controls.
static String bold2(s, var) {
    if (var == null) return "<b>$s (N/A)</b>"
    else return ("<b>$s ($var)</b>")
}

//Functions to enhance text appearance
static String bold(s) { return "<b>$s</b>" }

//Set the Section Titles to a consistent style.
static def sectionTitle(title) { return "<span style='color:#000000; margin-top:1em; font-size:16px; box-shadow: 0px 0px 3px 3px #40b9f2; padding:1px; background:#40b9f2;'><b>${title}</b></span>" }

//Set the body text to a consistent style.
static String body(myBody) { return "<span style='color:#17202A;text-align:left; margin-top:0em; margin-bottom:0em ; font-size:18px'>" + myBody + "</span>&nbsp" }

//Produce a horizontal line of the specified width
static String line(myHeight) { return "<div style='background:#005A9C; height: " + myHeight.toString() + "px; margin-top:0em; margin-bottom:0em ; border: 0;'></div>" }

static String dodgerBlue(s) { return '<font color = "DodgerBlue">' + s + '</font>' }

static String red(s) { return '<font color = "Red">' + s + '</font>' }

// Convert HSV to RGB using the values in the received map.
def getHEXfromHSV(hsvMap){
	//log.info ("getHEXfromHSV: hsvMap is: $hsvMap")
    def myRGB = hubitat.helper.ColorUtils.hsvToRGB([hsvMap.hue, hsvMap.saturation, hsvMap.value])
    def HEX = hubitat.helper.ColorUtils.rgbToHEX(myRGB)
    //log.info ("New HEX Color is: ${HEX}")
    return HEX
}

// Iterate over each device in myLights and check if deviceID matches. If it does match return that device.
def findDeviceById(deviceID) {
    def foundDevice = null  // Variable to store the found device
    myLights.each { device ->
        if (device.getId() == deviceID) {
            foundDevice = device
            return foundDevice
        }
    }
    return foundDevice
}

//Receives a 6 digit hex color and an opacity and converts them to HEX8
def convertToHex8(String hexColor, float opacity) {
    if (isLogTrace) log.trace("<b>Entering convertToHex8: With $hexColor  $opacity</b>")
    if (hexColor != null) hexColor = hexColor.replace("#", "")
    // Ensure opacity is within the range 0 to 1
    opacity = Math.max(0, Math.min(1, opacity))
    // Convert the Hex color to HEX8 format
    def red = Integer.parseInt(hexColor.substring(0, 2), 16)
    def green = Integer.parseInt(hexColor.substring(2, 4), 16)
    def blue = Integer.parseInt(hexColor.substring(4, 6), 16)
    def alpha = Math.round(opacity * 255).toInteger()
    // Format the values as a hex string
    def Hex8 = String.format("#%02X%02X%02X%02X", red, green, blue, alpha)
    return Hex8
}

//Convert [HTML] tags to <HTML> for display.
def toHTML(HTML) {
    if (HTML == null) return ""
    myHTML = HTML.replace("[", "<")
    myHTML = myHTML.replace("]", ">")
    return myHTML
}



//*******************************************************************************************************************************************************************************************
//**************
//**************  End Utility Functions
//**************
//*******************************************************************************************************************************************************************************************



//*******************************************************************************************************************************************************************************************
//**************
//**************  Standard System Elements
//**************
//*******************************************************************************************************************************************************************************************

//Configures all of the default settings values. This allows us to have some parts of the settings not be visible but still have their values initialized.
//We do this to avoid errors that might occur if a particular setting were referenced but had not been initialized.
def initialize() {
    if (state.initialized == true) {
        if (isLogTrace) log.trace("<b>initialize: Initialize has already been run. Exiting</b>")
        //return
    }
    log.trace("<b>Running Initialize</b>")

	//Remote Settings
	app.updateSetting("mySelectedRemote", "")
	
	//Tile Size
	app.updateSetting("tilePreviewWidth", "3")
    app.updateSetting("tilePreviewHeight", "2")
	app.updateSetting("tilePreviewBackground", [value: "#696969", type: "color"])
		
	//Global Settings
	app.updateSetting("invalidAttribute", [value: "N/A", type: "enum"])
	app.updateSetting("defaultDateTimeFormat", 3)
	app.updateSetting("defaultDurationFormat", 21)
	
	//Table Properties
	app.updateSetting("thp", "3")
	app.updateSetting("tvp", "3")
		
	//Info Column Properties
	app.updateSetting("hideColumn6", true)
	app.updateSetting("ih1", "Last On")
	app.updateSetting("info1Source", "lastOn")
	app.updateSetting("its1", "80")
	app.updateSetting("ita1", "Center")
	app.updateSetting("showSeconds1", false)
	
	app.updateSetting("hideColumn7", true)
	app.updateSetting("ih2", "Last On Duration")
	app.updateSetting("info2Source", "lastOnDuration")
	app.updateSetting("its2", "80")
	app.updateSetting("ita2", "Center")
	app.updateSetting("showSeconds2", false)
	
	 //Title Properties
    app.updateSetting("tt", "?")
    app.updateSetting("ts", "125")
	app.updateSetting("tp", "5")
    app.updateSetting("tc", [value: "#000000", type: "color"])
	app.updateSetting("tb", [value: "#8FC126", type: "color"])
    app.updateSetting("ta", "Center")
	app.updateSetting("to", "1")
    	
	//Header Properties
	app.updateSetting("hts", "100")
	app.updateSetting("htc", [value: "#000000", type: "color"])
	app.updateSetting("hbc", [value: "#8FC126", type: "color"])
	app.updateSetting("hbo", "1")
	app.updateSetting("c2header", "Name")
    
	//Row Properties
	app.updateSetting("rts", "90")
	app.updateSetting("rtc", [value: "#000000", type: "color"])
    app.updateSetting("rbc", [value: "#D9ECB1", type: "color"])
	app.updateSetting("rbs", [value: "#FFFFC2", type: "color"])
	app.updateSetting("rbo", "1")
	
	//Column Properties
	app.updateSetting("hideColumn1", false)
	app.updateSetting("hideColumn3", false)
	app.updateSetting("hideColumn4", false)
	app.updateSetting("hideColumn5", false)
	
    
    //Publishing
    app.updateSetting("mySelectedRemote", "")
    app.updateSetting("publishEndpoints", [value: "Local", type: "enum"])
    app.updateSetting("eventTimeout", "2000")
    
    //Create Access Points
    createAccessToken()
    state.localEndpoint = "${getFullLocalApiServerUrl()}/tb?access_token=${state.accessToken}"
    state.cloudEndpoint = "${getFullApiServerUrl()}/tb?access_token=${state.accessToken}"
	state.localEndpointData = "${getFullLocalApiServerUrl()}/tb/data?access_token=${state.accessToken}"
    state.cloudEndpointData = "${getFullApiServerUrl()}/tb/data?access_token=${state.accessToken}"
		
	//Set initial Log settings
	app.updateSetting('isLogConnections', false)
	app.updateSetting('isLogActions', true)
	app.updateSetting('isLogPublish', false)
	app.updateSetting('isLogDeviceInfo', false)
	app.updateSetting('isLogError', true)
    app.updateSetting('isLogDebug', false)
    app.updateSetting('isLogTrace', false)
    
	//Have all the sections collapsed to begin with except devices
    state.hidden = [Device: false, Endpoints: true, Design: false, Publish: true]
    
    state.initialized = true
	
}

def updated(){
    if(!state?.isInstalled) { state?.isInstalled = true }
}


//*******************************************************************************************************************************************************************************************
//**************
//**************  End Standard System Elements
//**************
//*******************************************************************************************************************************************************************************************





//*******************************************************************************************************************************************************************************************
//**************
//**************  Remote Control APPlet Code
//**************
//*******************************************************************************************************************************************************************************************

//This contains the whole HTML\CSS\SVG\SCRIPT file equivalent. Placing it in a function makes it easy to collapse.
static def myHTML() {
def HTML = 
'''
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🎛️ #BrowserTitle#</title>
    <style>
		html, body {
			display: flex; /* Flexbox layout */
			align-items: #va#; /* Vertical Alignment */
			justify-content: #ha#; /* Horizontally Alignment */
			padding: 7px; //7px is optimal for viewing the 3 dots menu.
			overflow: hidden;
			height:100%;
		}

		/* This is the top level containerCentering the content inside table-content */
		.container {
			border: 1px solid rgba(0, 0, 0, 0);
			max-width: 800px;
		}

		/* START OF TITLE CLASSES */
		.title{
			padding: #tp#px;
			text-align: #ta#;
			font-size: #ts#%; 
			font-weight: 400;
			color: #tc#;
			background-color: #tb#;
			display: #titleDisplay#;
		}
				
		/*  START OF TABLE CLASSES */
        table {
            width: 100%;
            border-collapse: collapse;
            table-layout: auto;	
        }

        th, td {
			padding: #tvp#px #thp#px;
            cursor: pointer;
            text-align: center;
			vertical-align: center;
			border: 1px solid black;
        }

        th:nth-child(1), td:nth-child(1) { width: 6%; display:#hideColumn1#; }
		th:nth-child(2) { padding-left:5px; text-align:left}, td:nth-child(2) {text-align:left; padding-left:2px; }
		/*th:nth-child(2) { text-align:left}, td:nth-child(2) {text-align:left; }*/
        th:nth-child(3), td:nth-child(3) { display:#hideColumn3#; }
        th:nth-child(4), td:nth-child(4) { display:#hideColumn4#; }
		th:nth-child(5), td:nth-child(5) { display:#hideColumn5#; }
		th:nth-child(6), td:nth-child(6) { display:#hideColumn6#;}
		th:nth-child(7), td:nth-child(7) { display:#hideColumn7#;}

        th { background-color: #hbc#; 
			font-weight: bold; 
			font-size: #hts#%; 
			color: #htc#; 
			margin:1px;
			}

		tr { background-color: #rbc#;}

        tr:hover { background-color: #rbs#; }
		.selected-row {	background-color: #rbs#; }

		/*  START OF CONTROLS CLASSES */			
		/* Column 1 Checkboxes */
        input[type="checkbox"] {
            cursor: pointer;
			height:20px;
			width:15px;
			margin:0px;
			margin-top:3px;
        }
		
		/* Column 2 Device Names */
		.editable-input {
            border: none;
            width: 95%;
            background: transparent;
			font-size: #rts#%;
			color: #rtc#;
        }
		
		/* Column 3 On/Off Switch */
		.toggle-switch {
            position: relative;
            display: inline-block;
			vertical-align: center;
            margin-top: 5px;
			margin-bottom:2px;
            width: 30px;
            height: 15px;
            background-color: #ccc;
            border-radius: 7.5px;
            cursor: pointer;
            transition: background-color 0.3s ease;
            box-shadow: 0 0 5px #aaa;
        }

        .toggle-switch::before {
            content: '';
            position: absolute;
            width: 13px;
            height: 13px;
            border-radius: 50%;
            background-color: white;
            top: 1px;
            left: 1px;
            transition: transform 0.3s ease;
        }

        .toggle-switch.on {
            background-color: #2196F3;
            box-shadow: 0 0 10px rgba(255, 255, 0, 1);
        }

        .toggle-switch.on::before {
            transform: translateX(15px);
        }

		/* Column 4 Color Control */
		.colorPicker{
	    	border: 2px solid #888;
	    	border-radius: 2px;
	    	width: 40px;
	    	height: 20px;
	    	cursor: pointer;  
		}	

		input[type="color"]::-webkit-color-swatch-wrapper {
			padding: 0px;
		}

        /* Column 5 Dimmer and Kelvin Sliders */
		.slider-container {
			position: relative; /* Make the container a relative positioning context */
			width: 100%;        /* Ensure the container spans the full cell width */
			display: flex;
			justify-content: center; /* Center the slider horizontally */
			align-items: center;     /* Center everything vertically */
			background-color:#rbc#;
		}

		.colorTemperature-slider, .level-slider {
			width: 90%;  /*Adjust the width of the slider as needed */   
			opacity:0.75;
		}

		.colorTemperature-value, .level-value {
			position: absolute;  /* Position the value on top of the slider */
			top: 50%;            /* Adjust this to position the value over the slider */
			transform: translateY(-50%); /* Center vertically */
			font-size: 90%;     /* Adjust the font size as needed */
			pointer-events: none; /* Prevent the value from interfering with slider interaction */
			text-align: center;  /* Center the text horizontally */
		}
 
        /* Custom background color for WebKit-based browsers (Chrome, Safari) */
        .colorTemperature-slider::-webkit-slider-runnable-track {
			background: linear-gradient(to right, 
			#FF4500 0%,   /* Candlelight (1900K) */
			#FFA500 16%,  /* Incandescent (2400K) */
			#FFD700 33%,  /* Warm White (2700K) */
			#FFFACD 49%,  /* Soft White (3000K) */
			#FFFFE0 60%,  /* Neutral White (3500K) */
			#F5F5F5 66%,  /* Cool White (4000K) */
			#FFFFFF 80%,  /* Bright White (5000K) */
			#ADD8E6 100%  /* Daylight (6500K) */
			);
			border-radius:0px;
			height:16px;
			outline: 2px solid #888;
        }

        /* Custom background color for WebKit-based browsers (Chrome, Safari) */
        .level-slider::-webkit-slider-runnable-track {
			background: linear-gradient(to right, 
            #000000 0%,   /* Dark (0%) */
            #2B2B2B 15%,  /* Very Dark Gray */
            #555555 30%,  /* Dark Gray */
            #808080 45%,  /* Medium Gray */
            #AAAAAA 60%,  /* Light Gray */
            #D5D5D5 75%,  /* Very Light Gray */
            #FFFFFF 100%  /* Bright White (100%) */
            );
			border-radius:0px;
			height:16px;
			outline: 2px solid #888;
        }	

		/* Column 6 Info */
		.info1 {
			font-size:#its1#%;
			text-align:#ita1#;
            border: none;
            background: transparent;
			color: #rtc#;
			/*white-space:nowrap;*/
        }

		/* Column 7 Info */
		.info2 {
			font-size:#its2#%;
			text-align:#ita2#;
            border: none;
            background: transparent;
			color: #rtc#;
			/*white-space:nowrap;*/
        }

	/* Define an orange glow effect */	
	.glow-effectCT {outline: 2px solid #000;}
	.glow-effectRGB {border: 2px solid #000;}

    </style>
</head>
<body>

//outline: none;
//box-shadow: 0 0 5px 3px rgba(0, 153, 255, 1);
//border: 2px solid red; //Not visible on the slider.
//outline: 2px solid black; //very inconsistent between the two controls.

<div class="container">
	<div class="title" id="title">#tt#</div>
	<table id="sortableTable">
		<thead>
			<tr>
				<th><input type="checkbox" id="masterCheckbox" onclick="toggleAllCheckboxes(this)" onchange="updateJSONOutput()" title="Select All/Deselect All"></th>
				<th id="nameHeader" class="sortLinks" onclick="sortTable(1);">
    				<span class="header-title" title="Sort Name A-Z">#c2header#</span>
    				<span class="refresh-icon" style="float: right;" onclick="event.stopPropagation(); refreshPage(50);" title="Refresh Data">↻ </span>
				</th>
				<th id="stateHeader" class="sortLinks" onclick="sortTable(2);"><span class="header-title" title="Sort State On-Off">State</span></th>
				<th id="color">Color</th> 
				<th id="dimmerKelvinHeader" class="sortLinks" onclick="toggleSlider()" title="Toggle Dimmer/Kelvin">Dim/°K</th>
				<th id="info1" class="sortLinks" title="Info1">#Info1#</th>
				<th id="info2" class="sortLinks" title="Info2">#Info2#</th>
			</tr>
		</thead>
		<tbody><!-- Table rows will be dynamically loaded from JSON --></tbody>
	</table>
</div>
	
<script>
														  
// Retrieve values from localStorage or initialize to default for the active column and direction
let storedSortDirection = JSON.parse(localStorage.getItem("sortDirection"));
let sortDirection = storedSortDirection || { activeColumn: 1, direction: 'asc' };
let showSlider = (localStorage.getItem("showSlider") === "Dimmer" || localStorage.getItem("showSlider") === "Kelvin") ? localStorage.getItem("showSlider") : "Dimmer";
let lastCommand = "None";

//Performs a complete refresh of the page.
function refreshPage(timeout) {
		setTimeout(function() {
			location.reload(true);  // Force reload from the server
		}, timeout);  // 1000 milliseconds = 1 second
	}

// Function to fetch JSON data from a URL and return it
async function fetchData() {
	//console.log("fetchData is being called");
	const url = '#URL#'; // Example URL
	try {
		const response = await fetch(url); // Fetch the data from the URL
		const jsonData = await response.json(); // Parse the response as JSON
		return jsonData; // Return the JSON data
	} catch (error) {
		console.error("Error fetching data:", error);
		return null; // Return null if there's an error
	}
}

// Call the function and handle the returned data
function initialize() {
	fetchData().then(data => {
		if (data) {
			console.log("Fetched JSON Data:", data);
			loadTableFromJSON(data);
			attachSortListeners(); // Attach sort event listeners
			sortTable(-1);  //-1 Indicates to use the saved sort order
			attachMasterCheckboxListener(); // Attach master checkbox listener
		} else {
			console.log("Failed to fetch data.");
		}
	});		
}

function loadTableFromJSON(myJSON) {
    const tbody = document.querySelector("#sortableTable tbody");
    tbody.innerHTML = ""; // Clear existing table rows
    myJSON.forEach((item, index) => {
		//console.log("Item type is:", item.type)
        const row = document.createElement('tr');
        
        // Add deviceID and type as attributes in the row for later use.
        row.dataset.deviceId = item.deviceID;
        row.dataset.type = item.type;
		row.dataset.colorMode = item.colorMode != null ? item.colorMode : "None";  //colorMode will only be populated if the Type is 4.
		row.dataset.info1 = item.info1;
		row.dataset.info2 = item.info2;
		//console.log("Row Dataset is:", row.dataset);
        
        // Color Input
        const colorPickerHTML = item.type >= 4																 
            ? `<input type="color" class="colorPicker" id="colorInput${index}" value="${item.color}" onchange="updateColor(${index}); updateJSONOutput()">`
            : ``;  // No color picker for type < 4 

        // Generate both sliders and hide the one not in use using CSS
        const sliderHTML = `
            <div class="slider-container">
                <!-- Level Slider -->
                <input type="range" class="level-slider" min="0" max="100" value="${item.level}" 
                    style="display: ${showSlider === 'Dimmer' && item.type >= 2 ? 'block' : 'none'}" 
                    oninput="updateDimmerValue(this)" onchange="updateJSONOutput()">
                <span class="level-value" style="color:white; display: ${showSlider === 'Dimmer' && item.type >= 2 ? 'block' : 'none'}">${item.level}%</span>
                
                <!-- Kelvin Slider -->
                <input type="range" class="colorTemperature-slider" min="2000" max="6500" value="${item.colorTemperature}" 
                    style="display: ${showSlider === 'Kelvin' && ( item.type === 3 || item.type === 5 ) ? 'block' : 'none'}" 
                    oninput="updateKelvinValue(this)" onchange="updateJSONOutput()">
                <span class="colorTemperature-value" style="color:black; display: ${showSlider === 'Kelvin' && (item.type === 3 || item.type === 5) ? 'block' : 'none'}">${item.colorTemperature}°K</span>
            </div>`;

        row.innerHTML = `
            <td><input type="checkbox" class="option-checkbox" onchange="toggleRowSelection(this); updateJSONOutput()"></td>
            <td><input type="text" class="editable-input" value="${item.name}" onchange="updateJSONOutput()" readonly></td>
            <td><div class="toggle-switch ${item.switch === 'on' ? 'on' : ''}" data-state="${item.switch}" onclick="toggleSwitch(this); updateJSONOutput()"></div></td>
            <td>${colorPickerHTML}</td>
            <td>${sliderHTML}</td>
			<td><div class="info1">${item.info1}</td>
			<td><div class="info2">${item.info2}</td>
        `;

        tbody.appendChild(row);
    });

    // Re-attach sorting and checkbox listeners after the table is loaded
    attachSortListeners();
    attachMasterCheckboxListener();
	setGlowEffect();
}

// Sort the table by column index. If it receives a columIndex of -1 then use the last saved values for column and direction.
function sortTable(columnIndex) {
    const tbody = document.querySelector("#sortableTable tbody");
    const rows = Array.from(tbody.rows);

    // If columnIndex is -1, load the column index and direction from localStorage
    if (columnIndex === -1) {
        columnIndex = sortDirection.activeColumn;  // Load active column from localStorage
    } else {
        // Update the active column and toggle the sort direction for the received column index
        sortDirection.activeColumn = columnIndex;
        sortDirection.direction = sortDirection.direction === 'asc' ? 'desc' : 'asc'; // Toggle direction
    }

    // Get the direction for the active column
    const direction = sortDirection.direction;

    // Save the updated sortDirection (active column and direction) to localStorage
    localStorage.setItem("sortDirection", JSON.stringify(sortDirection));
    
    // Sort the rows based on the column index and direction
    rows.sort((a, b) => {
        let cellA, cellB;

        // Custom sorting logic for each column type
        if (columnIndex === 1) {  // Name column (input text)
            cellA = a.cells[columnIndex].querySelector('input').value.toLowerCase();
            cellB = b.cells[columnIndex].querySelector('input').value.toLowerCase();
        } else if (columnIndex === 2) {  // State column (toggle switch)
            cellA = a.cells[columnIndex].querySelector('.toggle-switch').dataset.state;
            cellB = b.cells[columnIndex].querySelector('.toggle-switch').dataset.state;
        }

        // Compare based on the sorting direction
        if (direction === 'asc') { return cellA > cellB ? 1 : -1; } 
		else { return cellA < cellB ? 1 : -1; }
    });

    // Append the sorted rows back to the table body
    tbody.append(...rows);
}


// Gather all table data into a JSON output
function updateJSONOutput() {
    const rows = document.querySelectorAll("#sortableTable tbody tr");
    const output = [];

    rows.forEach((row) => {
        const checkbox = row.querySelector('td:nth-child(1) input[type="checkbox"]').checked;
        const name = row.querySelector('td:nth-child(2) input').value;
        const mySwitch = row.querySelector('.toggle-switch').dataset.state;
        const type = Number(row.dataset.type);
        const deviceID = row.dataset.deviceId;
        let color = '#000000'; // Default color
        const level = row.querySelector('.level-slider') ? row.querySelector('.level-slider').value : 0;
        const colorTemperature = row.querySelector('.colorTemperature-slider') ? row.querySelector('.colorTemperature-slider').value : 0;

        // Retrieve the color from the input[type="color"] element
        const colorInput = row.querySelector('input[type="color"]');
        color = colorInput ? colorInput.value : '#000000'; // Use the color value directly

        const outputData = {
            name: name,
            deviceID: deviceID,
            type: type
        };

        // Conditionally add fields based on type
        if (type >= 1 && lastCommand === "switch") { outputData.switch = mySwitch }
        if (type >= 2 && lastCommand === "level") { outputData.level = parseInt(level) }
        if ( (type === 3 || type === 5) && lastCommand === "colorTemperature") { outputData.colorTemperature = parseInt(colorTemperature) }
		if (type >= 4 && lastCommand === "color") {outputData.color = color.toUpperCase() } // Make sure the color is in uppercase (hex)

        // Push data object into output array
        output.push(outputData);
    });

    //console.log("output is: ", output);
    // Send data to the backend
    sendData(JSON.stringify(output));
}


function rgb2hex(rgbString) {
    let rgbArray = rgbString.replace('rgb(', '').replace(')', '').split(',');
    let r = parseInt(rgbArray[0].trim());
    let g = parseInt(rgbArray[1].trim());
    let b = parseInt(rgbArray[2].trim());
    function hex(x) { return ("0" + x.toString(16)).slice(-2); }
    return `#${hex(r)}${hex(g)}${hex(b)}`;
}

function sendData(payload) {
    const url = '#URL#';
    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: payload
    }).catch(error => {
        console.error('Error:', error);
    });
}

function toggleRowSelection(checkbox) {
	const row = checkbox.closest('tr');
	if (checkbox.checked) {
		row.classList.add('selected-row');  // Add the 'selected-row' class
	} else {
		row.classList.remove('selected-row');  // Remove the 'selected-row' class
	}
}

//Switch the last column between displaying Dimmer or Kelvin sliders
function toggleSlider() {
    // Toggle the showSlider value and save in localStorage
    showSlider = showSlider === "Dimmer" ? "Kelvin" : "Dimmer";
    localStorage.setItem("showSlider", showSlider);
    
    // Toggle visibility of level sliders and values (shown when showSlider is "Dimmer")
    document.querySelectorAll('.level-slider').forEach(slider => {
        slider.style.display = showSlider === "Dimmer" ? 'block' : 'none';
    });
    document.querySelectorAll('.level-value').forEach(value => {
        value.style.display = showSlider === "Dimmer" ? 'block' : 'none';
    });

    // Toggle visibility of colorTemperature sliders and values (shown when showSlider is "Kelvin")
    document.querySelectorAll('.colorTemperature-slider').forEach(slider => {
        slider.style.display = showSlider === "Kelvin" ? 'block' : 'none';
    });
    document.querySelectorAll('.colorTemperature-value').forEach(value => {
        value.style.display = showSlider === "Kelvin" ? 'block' : 'none';
    });
	
	hideUnusedSliders();
}

//Hide the sliders which are inappropriate for the device type (1-5) that is appended to the row.dataset.type 
function hideUnusedSliders() {
    // Helper function to hide sliders and values
    const hideElement = (element) => {
        if (element) element.style.display = 'none';
    };

    // Loop through all the rows in the table
    document.querySelectorAll('tr').forEach(row => {
		const type = Number(row.dataset.type); // Get the device type (1 - 5)

        // Hide the Dimmer slider and value if type <= 1
        if (type <= 1) {
            hideElement(row.querySelector('.level-slider'));
            hideElement(row.querySelector('.level-value'));
        }

        // Hide the colorTemperature slider and value if type <= 2 or type === 4 (RGB)
        if (type <= 2 || type === 4) {
            hideElement(row.querySelector('.colorTemperature-slider'));
            hideElement(row.querySelector('.colorTemperature-value'));
        }
    });
}

//style="display: ${showSlider === 'Kelvin' && ( item.type === 3 || item.type === 5 ) ? 'block' : 'none'}" 

// In order to distinguish whether a bulb was last set for a color or colorTemperature a glow is placed around the correct control.
function setGlowEffect() {
    // Loop through all the rows in the table
    document.querySelectorAll('tr').forEach(row => {
        const colorMode = row.dataset.colorMode; // Get the row's colorMode
        //console.log("colorMode is:", colorMode);

        // Define target elements for the current row
        const colorTempInput = row.querySelector('.colorTemperature-slider'); // For CT mode
        const colorInput = row.querySelector('input[type="color"]'); // For RGB mode

        // Remove any existing glow effect
        if (colorTempInput) colorTempInput.classList.remove('glow-effectCT');
        if (colorInput) colorInput.classList.remove('glow-effectRGB');

        // Apply glow effect for CT or RGB modes
        if (colorMode === 'CT' && colorTempInput) {
            colorTempInput.classList.add('glow-effectCT');
        }
        if (colorMode === 'RGB' && colorInput) {
			if (colorInput) colorInput.classList.add('glow-effectRGB');
        }
    });
}

function updateColor(index) {
    const colorInput = document.getElementById(`colorInput${index}`);
    const row = colorInput.closest('tr'); // Get the closest row element
    row.dataset.colorMode = "RGB"; // Set the color mode to "RGB" in the row's dataset

    synchronizeCheckedRows("color", colorInput.value);
    lastCommand = "color";
	setGlowEffect();
}


function updateKelvinValue(slider, index) {
    // Update the Kelvin value displayed next to the slider
    slider.nextElementSibling.innerText = slider.value + '°K';
    
    // Find the parent row (assuming each slider is inside a <tr> element)
    const row = slider.closest('tr');
    row.dataset.colorMode = "CT"; // Set colorMode to "CT" in the row's dataset

    // Synchronize the Kelvin value across checked rows
    synchronizeCheckedRows("colorTemperature", slider.value);
    lastCommand = "colorTemperature";
    setGlowEffect(); // Apply glow effect if needed
}

function updateKelvinValueOld(slider, index) {
    // Update the Kelvin value displayed next to the slider
    slider.nextElementSibling.innerText = slider.value + '°K';
    
    // Find the parent row (assuming each slider is inside a <tr> element)
    const row = slider.closest('tr');
    
    // Synchronize the Kelvin value across checked rows
    synchronizeCheckedRows("colorTemperature", slider.value);
	lastCommand = "colorTemperature";
	setGlowEffect();
}

function updateDimmerValue(slider) {
	slider.nextElementSibling.innerText = slider.value + '%';
	synchronizeCheckedRows("level", slider.value);
	lastCommand = "level";
}

function toggleSwitch(switchElement) {
	switchElement.classList.toggle('on');
	const newState = switchElement.classList.contains('on') ? 'on' : 'off';
	switchElement.dataset.state = newState;
	synchronizeCheckedRows("switch", newState);
	lastCommand = "switch";
}

function synchronizeCheckedRows(type, value) {
    const checkboxes = document.querySelectorAll('.option-checkbox');
    checkboxes.forEach((checkbox) => {
        const row = checkbox.closest('tr');
        if (checkbox.checked) {
            // Handle color input only if type is "color"
            if (type === "color") {
                const colorInput = row.querySelector('input[type="color"]');
                if (colorInput) { 
                    colorInput.value = value; 
					row.dataset.colorMode = "RGB";
                }
            } else if (type === "level") {
                const levelSlider = row.querySelector('.level-slider');
                if (levelSlider) {
                    levelSlider.value = value;
                    row.querySelector('.level-value').innerText = value + '%';
                }
            } else if (type === "colorTemperature") {
                const kelvinSlider = row.querySelector('.colorTemperature-slider');
                if (kelvinSlider) {
                    kelvinSlider.value = value;
                    row.querySelector('.colorTemperature-value').innerText = value + '°K';
					row.dataset.colorMode = "CT";
                }
            } else if (type === "switch") {
                const toggleSwitch = row.querySelector('.toggle-switch');
                if (toggleSwitch) {
                    if (value === 'on') {
                        toggleSwitch.classList.add('on');
                    } else {
                        toggleSwitch.classList.remove('on');
                    }
                    toggleSwitch.dataset.state = value;
                }
            }
        }
    });
    lastCommand = "checked";
}

// Attach sorting event listeners for columns 1 and 2 only
function attachSortListeners() {
    const headers = document.querySelectorAll("#sortableTable th");
    headers.forEach((header, index) => {
        // Only add event listeners for column index 1 or 2
        if (index === 1 || index === 2) { header.addEventListener("click", () => sortTable(index)); }
    });
}

// Toggle all checkboxes when master checkbox is clicked
function attachMasterCheckboxListener() {
	const masterCheckbox = document.querySelector("#masterCheckbox");
	masterCheckbox.addEventListener("change", () => {
		toggleAllCheckboxes(masterCheckbox);
	});
}

function toggleAllCheckboxes(masterCheckbox) {
	const checkboxes = document.querySelectorAll('.option-checkbox');
	checkboxes.forEach(checkbox => {
		checkbox.checked = masterCheckbox.checked;
		toggleRowSelection(checkbox);  // Ensure row color updates with selection
	});
}

document.addEventListener("DOMContentLoaded", function() {
	initialize();
});

</script>



</body>
</html>
'''
return HTML
}

//*******************************************************************************************************************************************************************************************
//**************
//**************  End Remote Control APplet Code
//**************
//*******************************************************************************************************************************************************************************************
   

