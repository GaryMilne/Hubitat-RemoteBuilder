/**
*  Remote Builder SmartGrid
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
*  Original posting on Hubitat Community forum: https://community.hubitat.com/t/release-remote-builder-a-new-way-to-control-devices-7-remotes-available/142060
*  Remote Builder Documentation: https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote%20Builder%20Help.pdf
*
*  Remote Builder - SmartGrid - ChangeLog
*  Version 1.3.1 - Initial Public Release
*  Version 1.3.2 - Improved sorting for State and Name columns to use a primary and secondary keys. Added sorting for the Info1 & Info2 columns.
*  Version 2.0.0 - Re-architected for polling operations. Added highlight to table when pollingUpdate received.
*  Version 2.0.X - Split variables between state and local storage. Added session tracking for each browser. Add local tracking of checkbox state. Changed appearance of shuttle. Added support for Valves and Locks. Added Icon column.
*  Version 2.1.X - Converted lastOpen\lastClosed lastLocked\lastUnlocked and lastOn\lastOff to all use lastActive\lastInactive
*  Version 2.2.X - Removed compiled versions of Cloud and Local content when the Endpoint is disabled
*  Version 2.3.X - Code cleanup. Adds Network type as an info column. Add Fan, Garage amd Shade control. 
*  Version 2.4.X - Fixed Color and CT highlights. Fixed various minor issues.Added Stretch option to the Horizontal Alignment and made it the default.
*  Version 2.5.0 - Improved slider controls and added Mozilla support (untested) and suppressed scrollbars for use in Hubitat Dashboards
*  Version 2.6.X - Added support for AudioVolume. Added 3rd Info column. Added scale for volume slider. 
*  Version 2.7.X - Split Shades and Blinds and added support for Tilt control. Improved appearance.
*  Version 2.8.0 - Moved Icon logic to Groovy. Added Mozilla support - untested.
*  Version 3.0.0 - Public release
* 
*  Gary Milne - December 27th, 2024 @ 8:35 PM V 3.0.0
*
**/

/* ToDo's before release
None
*/

/* Ideas for future releases
Add support for Thermostats
Add Sensors as dType 0 and use State\Control columns for data.
Add pause-play for polling and\or variable speed polling.
Add Scene Selector Control
Add Media Control
Add alternate polling indicator
Look into EventSocket\WebSocket
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

static def createDeviceTypeMap() {
    def dTypeMap = [ 1: "Switch", 2: "Dimmer", 3: "RGB", 4: "CT", 5: "RGBW", 10: "Valve", 11:"Lock", 12: "Fan", 13: "Garage Door", 14: "Shade", 15: "Blind", 16: "Volume" ]
    // Create the inverse map for name-to-number lookups
    def nameToNumberMap = dTypeMap.collectEntries { key, value -> [value, key] }
    return [dTypeMap: dTypeMap, nameToNumberMap: nameToNumberMap]
}

static def durationFormatsMap() { return [21: "To: Elapsed Time (dd):hh:mm:ss", 22: "To: Elapsed Time (dd):hh:mm"] }
static def durationFormatsList() { return durationFormatsMap().values() }

static def invalidAttributeStrings() { return ["N/A", "n/a", " ", "-", "--", "?", "??"] }
static def devicePropertiesList() { return ["lastActive", "lastInactive", "lastActiveDuration", "lastInactiveDuration", "roomName", "colorName", "colorMode", "power", "healthStatus", "energy", "dID", "network", "deviceTypeName"].sort() }

@Field static final codeDescription = "<b>Remote Builder - SmartGrid 3.0.0 (12/27/24)</b>"
@Field static final codeVersion = 300
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
	path("/tb/poll") { action: [POST: "", GET: "poll"] }
}

preferences {
   page name: "mainPage"
}

def mainPage(){
	
	//app.updateSetting("pollUpdateColor", [value: "#00FF00", type: "color"])
	//app.updateSetting("pollUpdateWidth", [value: "5", type: "enum"])
	//app.updateSetting("column3Header", "Name")
	
	if (state.variablesVersion == null || state.variablesVersion < codeVersion) updateVariables()
	
    //Basic initialization for the initial release. If it is already initialized then compile the remote on each reload.
    if (state.initialized == null) initialize()
	else compile()
	    
    dynamicPage(name: "mainPage", title: "<div style='text-align:center;color: #c61010; font-size:30px;text-shadow: 0 0 5px #FFF, 0 0 10px #FFF, 0 0 15px #FFF, 0 0 20px #49ff18, 0 0 30px #49FF18, 0 0 40px #49FF18, 0 0 55px #49FF18, 0 0 75px #ffffff;;'> Remote Builder - " + moduleName + " 💡 </div>", uninstall: true, install: true, singleThreaded:false) {
			section(hideable: true, hidden: state.hidden.Devices, title: buttonLink('btnHideDevice', getSectionTitle("Devices"), 20)) {
            	
				// Input for selecting filter criteria
				input(name: "filter", type: "enum", title: bold("Filter Devices"), options: ["All Selectable Devices", "Power Meters", "Switches", "Color Temperature Devices", "Color Devices", "Dimmable Devices", "Valves", "Fans", "Locks", "Garage Doors", "Shades & Blinds"].sort(), required: false, defaultValue: "Switch Devices", submitOnChange: true, width: 2, newLine: true, style:"margin-right: 20px")
				// Apply switch-case logic based on the filter value
    			switch (filter) {
        			case "All Selectable Devices":
						input "myDevices", "capability.powerMeter, capability.switch, capability.valve, capability.lock, capability.garageDoorControl, capability.fanControl, capability.audioVolume, capability.windowShade, capability.windowBlind", title: "<b>Select Devices</b>", multiple: true, submitOnChange: true
            			break
					case "Power Meters":
						input "myDevices", "capability.powerMeter", title: "<b>Select Power Meter Devices</b>", multiple: true, submitOnChange: true
            			break
					case "Switches":
						input "myDevices", "capability.switch", title: "<b>Select Switch Devices</b>", multiple: true, submitOnChange: true
            			break
			        case "Color Temperature Devices":
						input "myDevices", "capability.colorTemperature", title: "<b>Select Color Temperature Devices</b>", multiple: true, submitOnChange: true
            			break
        			case "Color Devices":
            			input "myDevices", "capability.colorControl", title: "<b>Select Color Devices</b>", multiple: true, submitOnChange: true
            			break
        			case "Dimmable Devices":
            			input "myDevices", "capability.switchLevel", title: "<b>Select Dimmable Devices</b>", multiple: true, submitOnChange: true
            			break
					case "Valves":
            			input "myDevices", "capability.valve", title: "<b>Select Valves</b>", multiple: true, submitOnChange: true
            			break
					case "Fans":
            			input "myDevices", "capability.fanControl", title: "<b>Select Fans</b>", multiple: true, submitOnChange: true
            			break
					case "Garage Doors":
            			input "myDevices", "capability.garageDoorControl", title: "<b>Select Garage Doors</b>", multiple: true, submitOnChange: true
            			break
					case "Locks":
            			input "myDevices", "capability.lock", title: "<b>Select Locks</b>", multiple: true, submitOnChange: true
            			break
					case "Shades & Blinds":
            			input "myDevices", "capability.windowShade, capability.windowBlind", title: "<b>Select Shades & Blinds</b>", multiple: true, submitOnChange: true
            			break
        			default:
            			if (isLogDebug) log.debug "No filter option selected."
    			}
				paragraph "<b>Important: If you change the selected devices you must do a " + red("Publish and Subscribe") + " for SmartGrid to work correctly.</b><br>"
			}
        
			//Start of Endpoints Section
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
				paragraph summary("Endpoint Help", myText)
            	paragraph line (2)
			}
				
			//Start of Polling Section
			section(hideable: true, hidden: state.hidden.Polling, title: buttonLink('btnHidePolling', getSectionTitle("Polling"), 20)) {	
				input(name: "isPollingEnabled", type: "enum", title: bold("Endpoint Polling"), options: ["Enabled", "Disabled"], required: false, defaultValue: "Disabled", submitOnChange: true, width: 2, style:"margin-right: 20px")
				myText = "<b>A)</b> You want a more graceful refresh operation on a Hubitat Dashboard. Enable Polling and set the Event Timeout (Publishing Section) to <b>Never</b>. Doing so results in the SmartGrid updating vs doing a complete refresh.<br>"
				myText += "<b>B)</b> You want an automatic refresh operation for a SmartGrid that is being displayed directly on a device without using a Hubitat Dashboard. In this case you should also enable Polling and set the Event Timeout to <b>Never</b>. This allows you to have a SmartGrid run directly on your phone, tablet or computer and it will automatically update whenever changes are detected.<br>"
				paragraph summary(red("<b>Important: When to Enable Polling</b><br>"), myText)
				
				if (isPollingEnabled == "Enabled"){
					input (name: "pollInterval", type: "enum", title: bold('Poll Interval (seconds)'), options: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '15', '20', '30', '60'], required: false, defaultValue: '5', width: 2, submitOnChange: true, newLine:true)
					input (name: "pollUpdateColorSuccess", type: "color", title: bold2("Update Color Success", pollUpdateColorSuccess), required: false, defaultValue: "#00FF00", submitOnChange: true, width:2)
					input (name: "pollUpdateColorFail", type: "color", title: bold2("Update Color Fail", pollUpdateColorFail), required: false, defaultValue: "#FF0000", submitOnChange: true, width:2)
					input (name: "pollUpdateWidth", type: "enum", title: bold('Update Width'), options: elementSize(), required: false, defaultValue: '5', width: 2, submitOnChange: true)
					input (name: "pollUpdateDuration", type: "enum", title: bold('Update Duration'), options: elementSize(), required: false, defaultValue: '2', width: 2, submitOnChange: true)
					input (name: "shuttleHeight", type: "enum", title: bold('Refresh Bar Height'), options: elementSize(), required: false, defaultValue: '2', width: 2, submitOnChange: true, newLine:true)
					input (name: "shuttleColor", type: "color", title: bold2("Refresh Bar Color", shuttleColor), required: false, defaultValue: "#000000", submitOnChange: true, width:2)
					input (name: "commandTimeout", type: "enum", title: bold('Command Timeout (seconds)'), options: ['5', '6', '7', '8', '9', '10', '12', '15', '20'], required: false, defaultValue: '10', width: 2, submitOnChange: true, newLine:true)
				}
				myText = "You can configure the SmartGrid to poll the endpoint and apply any changes that are found. If there are no changes the SmartGrid goes back to sleep until the next poll interval.<br>"
           		myText += "<b>Poll Interval:</b> The frequency at which the Hub will be contacted to ask if there are any updates available.<br>"
				myText += "<b>Poll Update Success Color:</b> When updates are applied the Grid will be outlined in the selected color.<br>"
				myText += "<b>Poll Update Failure Color:</b> When updates are requested but no changes are received within the command timeout period the Grid will be outlined in the selected color.<br>"
				myText += "<b>Poll Update Width:</b> The width of the outline in pixels when updates are applied.<br>"
				myText += "<b>Poll Update Duration:</b> The duration in seconds that the Success\\Failure outline is displayed.<br><br>"
				
				myText += "<b>Refresh Bar:</b> The Refresh Bar is displayed beneath the SmartGrid and is a visual indicator of the polling process. When the bar hits either edge then a polling event will occur and any changes will be picked up.<br>"
				myText += "<b>Refresh Bar Height:</b> The height of the bar beneath the SmartGrid that identifies the position in the polling cycle.<br>"
				myText += "<b>Refresh Bar Color:</b> The color of the bar beneath the SmartGrid that identifies the position in the polling cycle.<br>"
				myText += "<b>Command Timeout:</b> The amount of time allowed to pass without a response from the Hub before a request is deemed to have failed.<br>"
				myText += "When the polling process discovers an update is pending then the SmartGrid is refreshed and the table is outlined for 5 seconds using the Poll Update properties configured above.<br>"
				myText += "<b>Note: </b> You can initiate a full refresh of the table at anytime regardless of the polling interval using the Refresh Icon <b>↻</b>."
				paragraph summary("Polling Help", myText)
				paragraph line (2)
            }
        
			//Start of Design Section
            section(hideable: true, hidden: state.hidden.Design, title: buttonLink('btnHideDesign', getSectionTitle("Design"), 20)) {
                input(name: "displayEndpoint", type: "enum", title: bold("Endpoint to Display"), options: ["Local", "Cloud"], required: false, defaultValue: "Local", submitOnChange: true, width: 2, style:"margin-right:25px")
				input(name: "tilePreviewWidth", type: "enum", title: bold("Max Width (x200px)"), options: [1, 2, 3, 4, 5, 6, 7, 8], required: false, defaultValue: 2, submitOnChange: true, style: "width:12%;margin-right:25px")
                input(name: "tilePreviewHeight", type: "enum", title: bold("Preview Height (x190px)"), options: [1, 2, 3, 4, 5, 6, 7, 8], required: false, defaultValue: 2, submitOnChange: true, style: "width:12%;margin-right:25px")
				input(name: "tilePreviewBackground", type: "color", title: bold2("Preview Background Color", tb), required: false, defaultValue: "#000000", width: 3, submitOnChange: true, style: "margin-right:25px")
				if (myRemoteName != null && myRemote != null) input(name: "publishSubscribe", type: "button", title: "Publish and Subscribe", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, style:"margin-top:20px;margin-right:25px")
				
				//Add extra space for the padding.
				def maxWidth = (tilePreviewWidth.toInteger() * 200) + 50
            	def maxHeight = (tilePreviewHeight.toInteger() * 190) + 50
				
				if ( displayEndpoint == "Local" ) paragraph "<iframe src='${state.localEndpoint}' width='${maxWidth}' height='${maxHeight}' style='border:solid; margin-left:50px; padding:25px; background-color:${tilePreviewBackground};' scrolling='no' ></iframe>"
				if ( displayEndpoint == "Cloud" ) paragraph "<iframe src='${state.cloudEndpoint}' width='${maxWidth}' height='${maxHeight}' style='border:solid; margin-left:50px; padding:25px; background-color:${tilePreviewBackground};' scrolling='no'></iframe>"
								
				paragraph line(1)
				input (name: "customizeSection", type: "enum", title: bold("Customize"), required: false, options: ["General", "Title", "Headers & Columns", "Rows", "Info" ], defaultValue: "Table", submitOnChange: true, width: 2, newLineAfter:true)
				
				if (settings.customizeSection == "General") {
					input(name: "invalidAttribute", title: bold("Invalid Attribute String"), type: "enum", options: invalidAttributeStrings(), submitOnChange: true, defaultValue: "N/A", width: 2, style:"margin-right:25px")
					input(name: "defaultDateTimeFormat", title: bold("Date Time Format"), type: "enum", options: dateFormatsMap(), submitOnChange: true, defaultValue: 1, width: 2, style:"margin-right:25px")
					input(name: "defaultDurationFormat", title: bold("Duration Format"), type: "enum", options: durationFormatsMap(), submitOnChange: true, defaultValue: "(dd):hh:mm", width: 2, style:"margin-right:25px")
					//input(name: "controlSize", title: bold("Control Size"), type: "enum", options: ["Normal", "Large"], submitOnChange: true, defaultValue: "Normal", width: 2, style:"margin-right:25px")
					input(name: "controlSize", title: bold("Control Size"), type: "enum", options: ["15", "17.5", "20", "22.5", "25", "27.5", "30"], submitOnChange: true, defaultValue: "17.5", width: 2, style:"margin-right:25px")
					input (name: "ha", type: "enum", title: bold("Horizontal Alignment"), required: false, options: ["Stretch", "Left", "Center", "Right" ], defaultValue: "Stretch", submitOnChange: true, width: 2, style:"margin-right:25px", newLine: true)
                    input (name: "va", type: "enum", title: bold("Vertical Alignment"), required: false, options: ["Top", "Center", "Bottom" ], defaultValue: "Top", submitOnChange: true, width: 2, style:"margin-right:25px"  )
					input (name: "thp", type: "enum", title: bold("Horizontal Padding"), options: elementSize(), required: false, defaultValue: "3", submitOnChange: true, width: 2, style:"margin-right:25px" )
					input (name: "tvp", type: "enum", title: bold("Vertical Padding"), options: elementSize(), required: false, defaultValue: "3", submitOnChange: true, width: 2, style:"margin-right:25px" )
				}
				
				if (settings.customizeSection == "Title") {
                	input(name: "tt", type: "string", title: bold("Title Text (? to disable)"), required: false, defaultValue: "?", submitOnChange: true, width: 2, style:"margin-right:25px" )
                    input(name: "ts", type: "enum", title: bold("Text Size %"), options: textScale(), required: false, defaultValue: "150", submitOnChange: true, width: 2, style:"margin-right:25px" )
					input(name: "tp", type: "enum", title: bold("Text Padding"), options: elementSize(), required: false, defaultValue: "5", submitOnChange: true, width: 2, style:"margin-right:25px" )
                    input(name: "ta", type: "enum", title: bold("Text Alignment"), options: textAlignment(), required: false, defaultValue: "Center", submitOnChange: true, width: 2, style:"margin-right:25px" )
                    input(name: "tc", type: "color", title: bold2("Text Color", tc), required: false, defaultValue: "#000000", submitOnChange: true, width: 2, newLine:true, style:"margin-right:25px" )
					input(name: "tb", type: "color", title: bold2("Background Color", tb), required: false, defaultValue: "#000000", submitOnChange: true, width: 2, style:"margin-right:25px" )
					input(name: "to", type: "enum", title: bold("Background Opacity"), options: opacity(), required: false, defaultValue: "1", submitOnChange: true, width:2, style:"margin-right:25px" )
				}
				
				if (settings.customizeSection == "Headers & Columns") {
					input(name: "hts", type: "enum", title: bold("Text Size %"), options: textScale(), required: false, defaultValue: "125", width: 2, submitOnChange: true, style:"margin-right:30px; margin-left:10px")
                    input(name: "htc", type: "color", title: bold2("Text Color", htc), required: false, defaultValue: "#000000", width: 2, submitOnChange: true, style:"margin-right:30px")
                    input(name: "hbc", type: "color", title: bold2("Background Color", hbc), required: false, defaultValue: "#90C226", width: 2, submitOnChange: true, style:"margin-right:30px")
					input(name: "hbo", type: "enum", title: bold("Bg Opacity"), options: opacity(), required: false, defaultValue: "1", width:2, submitOnChange: true, style:"margin-right:30px")
					
					input(name: "hideColumn1", type: "bool", title: bold("Hide Selection Boxes?"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px;margin-right:30px; margin-left:10px", newLine:true)
					input(name: "hideColumn2", type: "bool", title: bold("Hide Icons?"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px;margin-right:30px")
					input(name: "hideColumn4", type: "bool", title: bold("Hide State?"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px;margin-right:30px")
					input(name: "column3Header", type: "string", title: bold("Column 3 Header"), required: false, defaultValue: "Name", width: 2, submitOnChange: true)
					
					input(name: "hideColumn5", type: "bool", title: bold("Hide Column 5 - Control A/B - Level/°K?"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px", newLine:true)
					if (hideColumn5 == false) input(name: "column5Header", type: "string", title: bold("Column 5 Header"), required: false, defaultValue: "Control A/B", width: 2, submitOnChange: true)
					input(name: "hideColumn6", type: "bool", title: bold("Hide Column 6 - Control C - Color?"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px", newLine:true)
					if (hideColumn6 == false) input(name: "column6Header", type: "string", title: bold("Column 6 Header"), required: false, defaultValue: "Control C", width: 2, submitOnChange: true)
					input(name: "hideColumn7", type: "bool", title: bold("Hide Column 7 - Info 1"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px", newLine:true)
					if (hideColumn7 == false) input(name: "column7Header", type: "string", title: bold("Info 1 Header Text"), required: false, defaultValue: "Activated", width: 2, submitOnChange: true)	
					input(name: "hideColumn8", type: "bool", title: bold("Hide Column 8 - Info 2"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px", newLine:true)
					if (hideColumn8 == false) input(name: "column8Header", type: "string", title: bold("Info 2 Header Text"), required: false, defaultValue: "Activated", width: 2, submitOnChange: true)	
					input(name: "hideColumn9", type: "bool", title: bold("Hide Column 9 - Info 3"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px", newLine:true)
					if (hideColumn9 == false) input(name: "column9Header", type: "string", title: bold("Info 3 Header Text"), required: false, defaultValue: "Activated", width: 2, submitOnChange: true)	
				}

				if (settings.customizeSection == "Rows") {
					input(name: "rts", type: "enum", title: bold("Text Size %"), options: textScale(), required: false, defaultValue: "100", submitOnChange: true, width:1)
                    input(name: "rtc", type: "color", title: bold2("Text Color", rtc), required: false, defaultValue: "#000000", submitOnChange: true, width:2)
                    input(name: "rbc", type: "color", title: bold2("Background Color", rbc), required: false, defaultValue: "#DDEEBB", submitOnChange: true, width:2)
					input(name: "rbo", type: "enum", title: bold("Background Opacity"), options: opacity(), required: false, defaultValue: "1", submitOnChange: true, width:2)
					input(name: "rbs", type: "color", title: bold2("Background Color - Selected Row", rbs), required: false, defaultValue: "#BFE373", submitOnChange: true, width:3)
				}
							
				if (settings.customizeSection == "Info") {
					input(name: "hideColumn7", type: "bool", title: bold("Hide Info Column 1"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px", newLine:true)
					if (hideColumn7 == false) {
						input(name: "column7Header", type: "string", title: bold("Info 1 Header Text"), required: false, width: 2, submitOnChange: true)	
						input(name: "its1", type: "enum", title: bold("Size %"), options: textScale(), required: false, defaultValue: "80", width: 1, submitOnChange: true)
						input(name: "ita1", type: "enum", title: bold("Alignment"), options: textAlignment(), required: false, defaultValue: "Center", width: 2, submitOnChange: true)
						input(name: "info1Source", type: "enum", title: bold("Data Source"), required: false, multiple: false, defaultValue: "lastActive", options: devicePropertiesList(), submitOnChange: true, width: 2)
					}
					input(name: "hideColumn8", type: "bool", title: bold("Hide Info Column 2"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px", newLine:true)
					if (hideColumn8 == false) {
						input(name: "column8Header", type: "string", title: bold("Info 2 Header Text"), required: false, width: 2, submitOnChange: true)
						input(name: "its2", type: "enum", title: bold("Size %"), options: textScale(), required: false, defaultValue: "80", width: 1, submitOnChange: true)
						input(name: "ita2", type: "enum", title: bold("Alignment"), options: textAlignment(), required: false, defaultValue: "Center", width: 2, submitOnChange: true)
						input(name: "info2Source", type: "enum", title: bold("Data Source"), required: false, multiple: false, defaultValue: "lastActiveDuration", options: devicePropertiesList(), submitOnChange: true, width: 2)
					}
					input(name: "hideColumn9", type: "bool", title: bold("Hide Info Column 3"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px", newLine:true)
					if (hideColumn9 == false) {
						input(name: "column9Header", type: "string", title: bold("Info 3 Header Text"), required: false, width: 2, submitOnChange: true)
						input(name: "its3", type: "enum", title: bold("Size %"), options: textScale(), required: false, defaultValue: "80", width: 1, submitOnChange: true)
						input(name: "ita3", type: "enum", title: bold("Alignment"), options: textAlignment(), required: false, defaultValue: "Center", width: 2, submitOnChange: true)
						input(name: "info3Source", type: "enum", title: bold("Data Source"), required: false, multiple: false, defaultValue: "roomName", options: devicePropertiesList(), submitOnChange: true, width: 2)
					}
				}
				
				paragraph line(2)
				paragraph "<b>Important: You must do a " + red("Publish and Subscribe") + " for SmartGrid to receive updates and work correctly in polling mode or to update automatically in the above window!</b><br>"
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
			myText += "<b>Note:</b> If you are not using the Remote within a Hubitat Dashboard you should <b>set the Event Timeout to Never</b> as republishing is not needed."
			paragraph summary("Publishing Help", myText)
            paragraph line(1)
			            
			if ( state.compiledLocal != null  && state.compiledCloud && settings.myRemote != null && myRemoteName != null) {
                input(name: "publishSubscribe", type: "button", title: "Publish and Subscribe", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2)
                input(name: "unsubscribe", type: "button", title: "Delete Subscription", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2)
            } else input(name: "cannotPublish", type: "button", title: "Publish and Subscribe", backgroundColor: "#D3D3D3", textColor: "black", submitOnChange: true, width: 2)			
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
            myDocURL = "<a href='https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote%20Builder%20SmartGrid%20Help%20-%20Version%203.0.pdf' target=_blank> <i><b>Remote Builder - SmartGrid 3.0 Help</b></i></a>"
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


//Used to update variables when upgrading software versions.
def updateVariables() {
    //This is a first time install so the 
    if (state.variablesVersion == null) {
        log.info("Initializing variablesVersion to: $codeVersion")
        state.variablesVersion = codeVersion
    }

    //Check to see if there has been an update. If there has the variablesVersion will be less than the codeVersion
    if (state.variablesVersion < codeVersion) {
        log.info("Updating Variables to $codeVersion")
        //Add the newly created variables here such as:
		//app.updateSetting("pollUpdateColor", [value: "#00FF00", type: "color"])
        //Make the variablesVersion the same as the codeVersion
		state.variablesVersion = codeVersion
		//Now re-compile the endpoints with the new variables
		compile()
    }
}

// Gets the state of the various lights that are being tracked and puts them into a JSON format for inclusion with the script 
def getJSON() {
    if (isLogTrace) log.trace("<b>Entering: GetJSON</b>")
    
    // List to hold device attribute data
    def deviceAttributesList = []
	def eventData = [:]
		    
    // Iterate through each device
    myDevices.each { device ->
        // Use LinkedHashMap to maintain the order of fields
        def deviceData = new LinkedHashMap()
		//Use a shared variable for the switchState
		def mySwitch = device.currentValue("switch")
        
        // Ensure 'name' and 'dID' are added first in the correct order
        deviceData.put("name", device.displayName)
        deviceData.put("dID", device.getId())
		
		//Check if the device has "Switch" capability. Attributes are switch - ENUM ["on", "off"]
		if (device.hasCapability("Switch")) {
			deviceData.put("dType", 1)
            deviceData.put("switch", mySwitch)
			deviceData.put("icon", getIcon(1, mySwitch))
        }		       

        // Check if the device has "SwitchLevel" capability. Attributes are: level - NUMBER, unit:%
        if (device.hasCapability("SwitchLevel")) {
			deviceData.put("dType", 2)
			def status = device.currentValue("level")?.toInteger() ?: 100
			deviceData.put("level", status)
			deviceData.put("icon", getIcon(2, mySwitch))
        }

		//If the device has ColorTemperature but NOT ColorControl then it is a CT only device. Attributes are: colorName - STRING colorTemperature - NUMBER, unit:°K
        if ( device.hasCapability("ColorTemperature") && !device.hasCapability("ColorControl") ) {
			deviceData.put("dType", 3)
			def status = device.currentValue("colorTemperature")?.toInteger() ?: 2000
			deviceData.put("CT", status)
			deviceData.put("icon", getIcon(3, mySwitch))
        }

		//If the device has ColorControl but NOT ColorTemperature then it is an RGB device. Attributes are: RGB - STRING color - STRING colorName - STRING hue - NUMBER saturation - NUMBER, unit:%
		if (device.hasCapability("ColorControl") && !device.hasCapability("ColorTemperature") ) {
			deviceData.put("dType", 4)
			def hsvMap = [hue: device.currentValue("hue") ?: 100, saturation: device.currentValue("saturation") ?: 100, value: device.currentValue("level")?.toInteger() ?: 100 ]
			def color = getHEXfromHSV(hsvMap)	
			deviceData.put("color", color)
			deviceData.put("icon", getIcon(4, mySwitch))
		}
		
		//If the device has ColorControl AND ColorTemperature then it is a RGBW device. Attributes are: RGB - STRING color - STRING colorName - STRING hue - NUMBER saturation - NUMBER, unit:% +++++ colorTemperature - NUMBER, unit:°K
        if ( device.hasCapability("ColorControl") && device.hasCapability("ColorTemperature") ) {
			deviceData.put("dType", 5)
			def hsvMap = [hue: device.currentValue("hue") ?: 100, saturation: device.currentValue("saturation") ?: 100, value: device.currentValue("level")?.toInteger() ?: 100 ]
			def color = getHEXfromHSV(hsvMap)	
			deviceData.put("color", color)
			deviceData.put("CT", (device.currentValue("colorTemperature")?.toInteger() ?: 2000))
			deviceData.put("colorMode", device.currentValue("colorMode"))
			deviceData.put("icon", getIcon(5, mySwitch))
        }
		
		//Check for valves - ENUM ["open", "closed"]
        if ( device.hasCapability("Valve") ) { 
			deviceData.put("dType", 10) 
			deviceData.put("switch", device.currentValue("valve") == "open" ? "on" : "off")
			deviceData.put("icon", getIcon(10, deviceData.get("switch")))
		}
		
		//Check for locks - states for lock are: ENUM ["locked", "unlocked with timeout", "unlocked", "unknown"]  //Only locked and unlocked are implemented.
        if ( device.hasCapability("Lock") ) { 
			deviceData.put("dType", 11) 	
			deviceData.put("switch", device.currentValue("lock") == "locked" ? "on" : "off")
			deviceData.put("icon", getIcon(11, deviceData.get("switch")))
		}
		
		//Check for Fans - States for speed are: ENUM ["low","medium-low","medium","medium-high","high","on","off","auto"]
        if ( device.hasCapability("FanControl") ) { 
			deviceData.put("dType", 12) 
			def status = device.currentValue("speed")
			deviceData.put("speed", status)
			deviceData.put("switch", device.currentValue("speed") == "off" ? "off" : "on")
			deviceData.put("icon", getIcon(12, deviceData.get("switch")))
		}
		
		//Check for Garage Doors - States are: ENUM ["unknown", "open", "closing", "closed", "opening"]
        if ( device.hasCapability("GarageDoorControl") ) { 
			deviceData.put("dType", 13) 
			def status = device.currentValue("door")
			deviceData.put("switch", status == "closed" ? "on" : "off")
			deviceData.put("door", status)
			deviceData.put("icon", getIcon(13, status))	
		}
				
		// Check for Shades and exclude blinds - States for windowShade are: ENUM ["opening", "partially open", "closed", "open", "closing", "unknown"]
		if (device.hasCapability("WindowShade") && !device.hasCapability("WindowBlind")) {
			deviceData.put("dType", 14) 
			def status = device.currentValue("windowShade")
			deviceData.put("windowShade", status)
			deviceData.put("position", device.currentValue("position"))
			deviceData.put("switch", status == "closed" ? "off" : "on")
			deviceData.put("icon", getIcon(14, status ) )
		}
		
		// Check for Blinds - States for windowBlind are: ENUM ["opening", "partially open", "closed", "open", "closing", "unknown"]
		if (device.hasCapability("WindowBlind")) {
			deviceData.put("dType", 15) 
			def status = device.currentValue("windowShade")
			deviceData.put("windowShade", status)
			deviceData.put("position", device.currentValue("position"))
			deviceData.put("tilt", Math.round(device.currentValue("tilt") * 0.9)) 
			deviceData.put("switch", status == "closed" ? "off" : "on")
			deviceData.put("icon", getIcon(15, status))
		}
		
		//Check for Audio Volume - States for Mute are: ENUM ["unmuted", "muted"]
        if ( device.hasCapability("AudioVolume") ) { 
			deviceData.put("dType", 16) 
			def status = device.currentValue("mute")
			deviceData.put("switch", status == "muted" ? "off" : "on")
			deviceData.put("volume", device.currentValue("volume"))
			deviceData.put("icon", getIcon(16, deviceData.get("switch")))
		}
		
		//Used for Icon debugging
		//log.info ("Status is: $status")
		//log.info ("Icon is: " + getIcon(15, status))
		
		deviceDetails = getDeviceInfo(device, deviceData.get("dType") )
						
		//Gather event information if needed for either of the two columns.
		if (hideColumn7 == false ) deviceData.put("info1", deviceDetails."${info1Source}")
		if (hideColumn8 == false ) deviceData.put("info2", deviceDetails."${info2Source}")
		if (hideColumn9 == false ) deviceData.put("info3", deviceDetails."${info3Source}")
			
        // Add device data to the list
        deviceAttributesList << deviceData
		//log.info("Device: $deviceData.name is dType: $deviceData.dType and data is: $deviceData")
    }
	    
    // Convert the list of device attributes to JSON format
    def jsonOutput = JsonOutput.toJson(deviceAttributesList)

    // Pretty print the JSON output
    def updatedJson = JsonOutput.prettyPrint(jsonOutput)
    
	if (isLogDebug) log.debug("getJSON Output: $updatedJson")
    state.JSON = updatedJson
}


// Return the appropriate icon to match the dType and deviceState
def getIcon(dType, deviceState) {
    def icons = [
        1  : [on: "toggle_on", off: "toggle_off"],
        2  : [on: "lightbulb", off: "light_off"],
        3  : [on: "lightbulb", off: "light_off"],
        4  : [on: "lightbulb", off: "light_off"],
        5  : [on: "lightbulb", off: "light_off"],
        10 : [on: "water_pump", off: "valve"],
        11 : [on: "lock", off: "lock_open"],
        12 : [on: "mode_fan", off: "mode_fan_off"],
        13 : [open: "garage", closed: "garage_door", opening: "arrow_upward", closing: "arrow_downward"],
        14 : [open: "window_closed", closed: "roller_shades_closed", opening: "arrow_upward", closing: "arrow_downward", 'partially open': "roller_shades"],
        15 : [open: "window_closed", closed: "blinds_closed", opening: "arrow_upward", closing: "arrow_downward", 'partially open': "blinds"],
        16 : [on: "volume_up", off: "volume_off"],
        20 : [on: "mode_cool", off: "mode_cool_off"], // Thermostat in cool mode
        21 : [on: "mode_heat", off: "mode_heat_off"], // Thermostat in heat mode
        22 : [on: "mode_dual", off: "mode_dual_off"], // Thermostat in dual mode
        23 : [on: "emergency_heat", off: "emergency_heat"] // Thermostat in emergency mode
    ]

    // Access the icon using get to avoid the call method issue
    return icons[dType]?.get(deviceState)
}


//Gets information about the lastActive, lastInactive etc and and put it into a map. Data from selected fields will be mapped into the Info columns.
def getDeviceInfo(device, dType){
	def lastActiveEvent
	def lastInactiveEvent
	def lastActive
	def lastInactive
	def lastActiveInstant
	def lastInactiveInstant
	
	def dID = device?.getId()
	def roomName = device?.getRoomName()
	def colorName = device?.currentValue("colorName")
	def colorMode = device?.currentValue("colorMode")
	def power = device?.currentValue("power")
	def healthStatus = device?.currentValue("healthStatus")
	def energy = device?.currentValue("energy")
	def dni = device?.getDeviceNetworkId()
	def network
	def deviceTypeName = getDeviceTypeInfo(dType)
	
	try {
		if (device.hasCapability("Switch")) {
			lastActiveEvent = device.events(max: 500) .findAll { it.name == "switch" && it?.value == "on" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
			lastInactiveEvent = device.events(max: 500) .findAll { it.name == "switch" && it?.value == "off" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
		}
	
		if (device.hasCapability("Valve")) {
			lastActiveEvent = device.events(max: 500) .findAll { it.name == "valve" && it?.value == "open" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
			lastInactiveEvent = device.events(max: 500) .findAll { it.name == "valve" && it?.value == "closed" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
		}
		
		if (device.hasCapability("Lock")) {
			lastActiveEvent = device.events(max: 500) .findAll { it.name == "lock" && it?.value == "locked" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
			lastInactiveEvent = device.events(max: 500) .findAll { it.name == "lock" && it?.value == "unlocked" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
		}
		
		if (device.hasCapability("FanControl")) {
			lastActiveEvent = device.events(max: 500) .findAll { it.name == "speed" && it?.value != "off" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
			lastInactiveEvent = device.events(max: 500) .findAll { it.name == "speed" && it?.value == "off" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
		}
		
		if (device.hasCapability("GarageDoorControl")) {
			lastActiveEvent = device.events(max: 500) .findAll { it.name == "door" && it?.value == "open" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
			lastInactiveEvent = device.events(max: 500) .findAll { it.name == "door" && it?.value != "open" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
		}
		
		if (device.hasCapability("WindowShade")) {
			lastActiveEvent = device.events(max: 500) .findAll { it.name == "windowShade" && it?.value == "open" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
			lastInactiveEvent = device.events(max: 500) .findAll { it.name == "windowShade" && it?.value != "open" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
		}
		
		if (lastActiveEvent != null) {
			lastActive = formatTime(lastActiveEvent?.getDate(), defaultDateTimeFormat.toInteger() ?: 3)
			lastActiveDuration = formatTime(lastActiveEvent?.getDate(), defaultDurationFormat.toInteger()  ?: 21 )
			lastActiveInstant = formatTime(lastActiveEvent?.getDate(), 0 )
		}
		
		if (lastInactiveEvent != null) {
			lastInactive = formatTime(lastInactiveEvent?.getDate(), defaultDateTimeFormat.toInteger() ?: 3)
			lastInactiveDuration = formatTime(lastInactiveEvent?.getDate(), defaultDurationFormat.toInteger()  ?: 21 )
			lastInactiveInstant = formatTime(lastInactiveEvent?.getDate(), 0 )
		}
		
		if (lastInactive != null && lastActive != null) {
			def durations = getDuration(lastActiveInstant, lastInactiveInstant)
			lastActiveDuration = durations.lastActiveDuration
			lastInactiveDuration = durations.lastInactiveDuration
		}
		
		network = getNetworkType(dni)
		
	}
	    
	catch (Exception exception) { log.error ("Exception is: $exception") }
	
	def result = [lastInactive: lastInactive, lastInactiveInstant: lastInactiveInstant, lastActive: lastActive, lastActiveInstant: lastActiveInstant, lastActiveDuration: lastActiveDuration, 
				  lastInactiveDuration: lastInactiveDuration, roomName: roomName, colorName: colorName, colorMode: colorMode, power: power, healthStatus: healthStatus, 
				  energy: energy, dID: dID, network: network, deviceTypeName: deviceTypeName ].collectEntries { key, value -> [key, value != null ? value : invalidAttribute.toString()] }
	
	//log.info("Returning map: $result")
    return result
	
}

// Function to determine network type based on DNI length
def getNetworkType(dni) {
    def networkTypes = [
        2: "Z-Wave",
        4: "Zigbee",
        8: "LAN",
        36: "Virtual"
    ]
    return networkTypes[dni?.length()] ?: "Other"
}

//Can return either a dType name or a dType number
def getDeviceTypeInfo(input) {
    def maps = createDeviceTypeMap()
    def dTypeMap = maps.dTypeMap
    def nameToNumberMap = maps.nameToNumberMap

    if (input instanceof Number) { return dTypeMap[input] ?: "Unknown device type number" } 
	else if (input instanceof String) { return nameToNumberMap[input] ?: "Unknown device type name" } 
	else { return "Invalid input" }
}

// Takes a JSON list of devices and changes, and applies them.
def applyChangesToDevices(changes) {
    if (isLogTrace) log.trace("<b>Entering: applyChangesToDevices</b>")
    if (isLogDeviceInfo) log.debug("Changes are: $changes")

    // Define a map of actions
    def commandMap = [ 'switch' : { dID, dType, newValue -> handleSwitch(dID, dType, newValue) }, 'level' : { dID, _, newValue -> dID.setLevel(newValue, 0.4) }, 'volume' : { dID, _, newValue -> dID.setVolume(newValue) },
        'position': { dID, _, newValue -> dID.setPosition(newValue) }, 'tilt' : { dID, _, newValue -> dID.setTiltLevel(Math.round(newValue * (100 / 90))) }, 'speed' : { dID, _, newValue -> dID.setSpeed(newValue) },
        'name'    : { dID, _, newValue -> dID.setLabel(newValue) }, 'CT' : { dID, _, newValue -> dID.setColorTemperature(newValue, null, 0.2) }, 'color' : { dID, _, newValue -> setDeviceColor(dID, newValue) }
    ]

    // Iterate over changes
    changes.each { change ->
        def device = findDeviceById(change.dID)
        if (device) {
            def dType = change.dType
            change.changes.each { key, values ->
                def newValue = values[1] // Get the new value for each property
                def command = commandMap[key]

                if (command) { command(device, dType, newValue) } 
				else { if (isLogDebug) log.warn("Unhandled change: $key") }
            }
        }
    }
}

// Handles switch-type actions for different device types
def handleSwitch(device, dType, newValue) {
    def actionMap = [
        [1, 2, 3, 4, 5]: { value -> value == "on" ? device.on() : device.off() },
        [10]: { value -> value == "on" ? device.open() : device.close() },
        [11]: { value -> value == "on" ? device.lock() : device.unlock() },
        [12]: { value -> value == "on" ? device.setSpeed("on") : device.setSpeed("off") },
        [13]: { value -> value == "on" ? device.close() : device.open() },
        [14, 15]: { value -> value == "on" ? device.open() : device.close() },
        [16]: { value -> value == "on" ? device.unmute() : device.mute() }
    ]
    
    // Iterate over the map to find the matching key
    actionMap.each { keys, action ->
        if (keys.contains(dType as Integer)) {
            action(newValue) // Execute the action if keys match
            return
        }
    }
}

// Converts a hex color to HSV and sets it for the device
def setDeviceColor(device, hexColor) {
    def RGB = hubitat.helper.ColorUtils.hexToRGB("$hexColor")
    def HSV = hubitat.helper.ColorUtils.rgbToHSV(RGB)
    device.setColor([hue: HSV[0], saturation: HSV[1], level: HSV[2]])
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

//Gets the duration between two instants (lastActive / lastInactive) that would typically be an on/off pairing.
def getDuration(long lastActiveEvent, long lastInactiveEvent) {
	if (isLogTrace) log.trace("<b>getDuration: Received $lastActiveEvent, $lastInactiveEvent</b>")
    def includeSeconds = false
    long diff = lastActiveEvent - lastInactiveEvent
	
	if (defaultDurationFormat.toInteger() == 21) includeSeconds = true
	else includeSeconds = false
	
	//Invalid
    if (diff == 0) return [currentRunTime: invalidAttribute.toString(), lastRunTime: invalidAttribute.toString() ]  //If there is no difference then the duration is not valid.
	
	//LastOn is more recent than LastOff so the device must be on and still running. So diff is equal to the amount of current runtime.
    if (diff > 0) {
        diff = (now() - lastActiveEvent) / 1000
		lastActiveDuration = "<span style='color: green;'>" + convertSecondsToDHMS(diff, includeSeconds).toString() + "</span>"
		//log.info ("Device is ON and current runtime is: $lastActiveDuration")
		
		diff = (lastActiveEvent - lastInactiveEvent) / 1000
		lastInactiveDuration = convertSecondsToDHMS(diff, includeSeconds).toString()
		//log.info ("Device is ON and last off duration is: $lastInactiveDuration")
    }  
	
	//LastOff is more recent than LastOn so the device must be off. So diff is equal to the amount of the last runtime (from lastActive -> lastInactive) 
    if (diff <= 0) {
        diff = (now() - lastInactiveEvent) / 1000
		lastInactiveDuration = "<span style='color: red;'>" + convertSecondsToDHMS(diff, includeSeconds).toString() + "</span>"
		
		diff = (lastInactiveEvent - lastActiveEvent) / 1000
		lastActiveDuration = "<span style='color: green;'>" + convertSecondsToDHMS(diff, includeSeconds).toString() + "</span>"
		lastActiveDuration = convertSecondsToDHMS(diff, includeSeconds).toString()
				
    }  
	result = [lastActiveDuration: lastActiveDuration, lastInactiveDuration: lastInactiveDuration ]
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
	if (isLogTrace) log.trace("<b>Entering: Compile</b>")
	if (isLogDebug) log.debug("Running Compile")
		
	def html1 = myHTML()
	def content = condense(html1)
	def localContent
	def cloudContent
	
	//if (controlSize == "Normal") content = content.replace('#controlSize#', "15" )
	//if (controlSize == "Large") content = content.replace('#controlSize#', "20" )
	content = content.replace('#controlSize#', controlSize.toString() )
	
	//Table Horizontal Alignment
	if (ha == "Stretch") content = content.replace('#ha#', "stretch" )
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
	
	//Column Headers
	content = content.replace('#column3Header#', column3Header )	// Column 3 header text
	content = content.replace('#column5Header#', column5Header )	// Column 5 header text
	content = content.replace('#column6Header#', column6Header )	// Column 6 header text
	
	//Info Columns
	content = content.replace('#Info1#', toHTML(column7Header) )	// Info 1 Header Text
	content = content.replace('#its1#', its1 )	// Info 1 Text Size
	content = content.replace('#ita1#', ita1 )	// Info 1 Text Alignment
	
	content = content.replace('#Info2#', toHTML(column8Header) )	// Info 2 Header Text
	content = content.replace('#its2#', its2 )	// Info 2 Text Size
	content = content.replace('#ita2#', ita2 )	// Info 2 Text Alignment
	
	content = content.replace('#Info3#', toHTML(column9Header) )	// Info 3 Header Text
	content = content.replace('#its3#', its3 )	// Info 3 Text Size
	content = content.replace('#ita3#', ita3 )	// Info 3 Text Alignment
	
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
	content = content.replace('#hideColumn2#', hideColumn2 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn4#', hideColumn4 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn5#', hideColumn5 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn6#', hideColumn6 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn7#', hideColumn7 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn8#', hideColumn8 ? 'none' : 'table-cell')
	content = content.replace('#hideColumn9#', hideColumn9 ? 'none' : 'table-cell')
		
	content = content.replace('#BrowserTitle#', myRemoteName)
	
	content = content.replace('#pollInterval#', (pollInterval.toInteger() * 1000).toString() )
	content = content.replace('#pollUpdateColorSuccess#', pollUpdateColorSuccess)
	content = content.replace('#pollUpdateColorFail#', pollUpdateColorFail)
	content = content.replace('#pollUpdateWidth#', pollUpdateWidth)
	content = content.replace('#pollUpdateDuration#', (pollUpdateDuration.toInteger() * 1000).toString() )
	content = content.replace('#commandTimeout#', (commandTimeout.toInteger() * 1000).toString() )
	
	if (isPollingEnabled == "Enabled") content = content.replace('#isPollingEnabled#', "true")
	if (isPollingEnabled == "Disabled") content = content.replace('#isPollingEnabled#', "false")
	
	content = content.replace('#shuttleColor#', shuttleColor)
	content = content.replace('#shuttleHeight#', shuttleHeight)
	
	//Put the proper statement in for the Materials Font. It's done this way because the cleaning of comments catches the // in https://
	content = content.replace('#MaterialsFont#', "<link href='https://fonts.googleapis.com/icon?family=Material+Symbols+Outlined' rel='stylesheet'>")
	
	content = content.replace('#maxWidth#', ( (tilePreviewWidth.toInteger() * 200).toString()) )
		
	if ( localEndpointState == "Enabled" ) localContent = content 
	if ( localEndpointState == "Disabled" ) localContent = "Local Endpoint Disabled" 
	if ( cloudEndpointState == "Enabled" ) cloudContent = content 
	if ( cloudEndpointState == "Disabled" ) cloudContent = "Cloud Endpoint Disabled" 
	
	localContent = localContent.replace("#URL#", state.localEndpointData )
	localContent = localContent.replace("#URL1#", state.localEndpointPoll )
	
	cloudContent = cloudContent.replace("#URL#", state.cloudEndpointData )
	cloudContent = cloudContent.replace("#URL1#", state.cloudEndpointPoll )
	
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
	//if (isLogDebug) log.debug ("Condense: After all comments with leading \\\\ are removed: " + input.size() + " bytes." )
    
    //Replace any comments in the SCRIPT section that will be between \* and *\  Note: Comments beginning with \\ will not be removed.
    input = input.replaceAll(/(?s)\/\*.*?\*\//, "")
			
    if (isLogDebug) log.debug  ("Condense: After SCRIPT comments removed: " + input.size() + " bytes." )
    if (isLogDebug) log.debug ("Condense: Before: " + String.format("%,d", initialSize) + " - After: " + String.format("%,d", input.size()) + " bytes.")
	
    return input 
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

// Allows the client to send the changed Device List and JSON to the Hub. dTypes are 1: switch, 2: switchLevel (dimmer), 3: colorTemperature, 4: colorControl - HSL, 5: colorControl - HSV
def toHub() {
	def sessionID = params.sessionID
	if (isLogTrace) log.trace ("<b>Entering toHub:</b> Session ID: $sessionID")
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
	
    // Map the second group by 'dID' for easier comparison
    def group2Map = group2.collectEntries { [(it.dID): it] }
    
    // Find changes
    def changes = []

    // Compare devices using dID as the constant
    group1.each { item1 ->
        def item2 = group2Map[item1.dID]
        if (item2) {  // If a matching dID is found
            def diff = [:]
            
            // Compare each key except for the dID and dType (which are constants)
            item1.each { key, value ->
                // Only compare if the key exists in both group1 and group2
                if (item2.containsKey(key) && key != "dID" && key != "dType") {
                    if (key == "CT") {
                        // Calculate the percentage difference
                        def oldTemp = item1.CT ?: 0
                        def newTemp = item2.CT ?: 0
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
                changes << [name: item2.name, dID: item1.dID, dType: item1.dType, changes: diff]  // Include dID and changes
            }
        }
    }

	// Print changes for each device one at a time
	changes.each { change ->
    	def key = change.changes.keySet().first()  // Get the key of the change (since there's only one)
    	def values = change.changes[key]           // Get the old and new values
    	if (isLogActions) log.info dodgerBlue("<b>Action: ${change.name} (ID: ${change.dID}): $key: ${values[0]} ---> ${values[1]} </b>")
	}

    // Apply all the changes to the devices.
    applyChangesToDevices(changes)
    result = render contentType: "application/json", data: "OK", status: 200
    return result
}

//Allows the client to get the Device List and JSON info from the Hub
def fromHub(){
	def sessionID = params.sessionID
	if (isLogTrace) log.trace ("<b>Entering fromHUB:</b> Session ID: $sessionID")
		
	//Get the latest JSON values.
	getJSON()
	
	//Update the session list to show that this session has downloaded the latest info.
	if (isLogDebug) log.debug("fromHub: Adding sessionID to updatedSessionList")
	if (sessionID != null) state.updatedSessionList << sessionID
	
	if (isLogDeviceInfo) log.info("<b>Downloading device data via fromHub():</b> $state.JSON")
	result = render contentType: "application/json", data: state.JSON, status: 200
	return result
}

//Receives a poll request from a session and 
def poll() {
    def sessionID = params.sessionID

    if (isLogTrace) log.trace("<b>Entering poll():</b> Session ID: $sessionID")
    
    def result
    if (!state.updatedSessionList.contains(sessionID)) {
        // If the list does NOT contain the sessionID then an update is needed.
		state.updatedSessionList << sessionID
        result = JsonOutput.toJson([update: true])
    } else {
        // If the list does contain the sessionID then no update is needed.
        result = JsonOutput.toJson([update: false])
    }

    result = render(contentType: "application/json", data: result, status: 200)
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
			state.localEndpointPoll = "${getFullLocalApiServerUrl()}/tb/poll?access_token=${state.accessToken}"
            state.cloudEndpointPoll = "${getFullApiServerUrl()}/tb/poll?access_token=${state.accessToken}"
			if (isLogDebug) log.debug ("Endpoints have been rebuilt")
			compile()
            break
		case "Compile":
            compile()
            break
        case "btnHideDevice":
            state.hidden.Devices = state.hidden.Devices ? false : true
            break
        case "btnHideEndpoints":
            state.hidden.Endpoints = state.hidden.Endpoints ? false : true
            break
		case "btnHidePolling":
            state.hidden.Polling = state.hidden.Polling ? false : true
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
	if (section == "Devices") {
        if (state.hidden.Devices == true) return sectionTitle("Devices ▶") else return sectionTitle("Devices ▼")
    }
    if (section == "Endpoints") {
        if (state.hidden.Endpoints == true) return sectionTitle("Endpoints ▶") else return sectionTitle("Endpoints ▼")
    }
	if (section == "Polling") {
        if (state.hidden.Polling == true) return sectionTitle("Polling ▶") else return sectionTitle("Polling ▼")
    }
    if (section == "Design") {
        if (state.hidden.Design == true) return sectionTitle("Design SmartGrid ▶") else return sectionTitle("Design SmartGrid ▼")
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
	def attributesToSubscribe = ["switch", "hue", "saturation", "level", "colorTemperature","valve","lock","speed","door","windowShade","position", "tilt", "mute","volume"]
	deleteSubscription()
	
	// Configure subscriptions
	myDevices.each { device ->
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
	
    //Handles the initialization of new variables added in code updates.
    if (state.variablesVersion == null || state.variablesVersion < codeVersion) updateVariables()
	
	//Change the flag used by the polling process to indicate a change has been detected.
	if (isLogDebug) log.debug("fromHub: Changing isPollUpdate to true.")
	state.isPollUpdate = true
	state.updatedSessionList = []
    
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

   // Regular expression for validating UUID format
def isValidUUID(address) {
    def uuidPattern = /^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$/
    return address ==~ uuidPattern
}

//Functions to enhance text appearance
static String bold(s) { return "<b>$s</b>" }

//Set the Section Titles to a consistent style.
static def sectionTitle(title) { return "<span style='color:#000; margin-top:1em; font-size:16px; box-shadow: 0px 0px 3px 3px #40b9f2; padding:1px; background:#40b9f2;'><b>${title}</b></span>" }

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

// Iterate over each device in myDevices and check if dID matches. If it does match return that device.
def findDeviceById(dID) {
    def foundDevice = null  // Variable to store the found device
    myDevices.each { device ->
        if (device.getId() == dID) {
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

//Set the notes to a consistent style.
static String summary(myTitle, myText) {
    myTitle = dodgerBlue(myTitle)
    return "<details><summary>" + myTitle + "</summary>" + myText + "</details>"
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
	
	//Device Selection
	app.updateSetting("filter", [value: "All Switch Devices", type: "enum"])

	//Endpoints and Polling
    createAccessToken()
    state.localEndpoint = "${getFullLocalApiServerUrl()}/tb?access_token=${state.accessToken}"
    state.cloudEndpoint = "${getFullApiServerUrl()}/tb?access_token=${state.accessToken}"
	state.localEndpointData = "${getFullLocalApiServerUrl()}/tb/data?access_token=${state.accessToken}"
    state.cloudEndpointData = "${getFullApiServerUrl()}/tb/data?access_token=${state.accessToken}"
	state.localEndpointPoll = "${getFullLocalApiServerUrl()}/tb/poll?access_token=${state.accessToken}"
    state.cloudEndpointPoll = "${getFullApiServerUrl()}/tb/poll?access_token=${state.accessToken}"

	app.updateSetting("isPollingEnabled", [value: "Enabled", type: "enum"])
	app.updateSetting("pollInterval", "3")
	app.updateSetting("pollUpdateColorSuccess", [value: "#00FF00", type: "color"])
	app.updateSetting("pollUpdateColorFail", [value: "#FF0000", type: "color"])
	app.updateSetting("pollUpdateWidth", [value: "3", type: "enum"])
	app.updateSetting("pollUpdateDuration", [value: "2", type: "enum"])
	app.updateSetting("shuttleColor", [value: "#99C5FF", type: "color"])
	app.updateSetting("shuttleHeight", [value: "2", type: "enum"])
		
	//Tile Size
	app.updateSetting("tilePreviewWidth", "3")
    app.updateSetting("tilePreviewHeight", "2")
	app.updateSetting("tilePreviewBackground", [value: "#696969", type: "color"])
		
	//Global Settings
	app.updateSetting("invalidAttribute", [value: "N/A", type: "enum"])
	app.updateSetting("defaultDateTimeFormat", 3)
	app.updateSetting("defaultDurationFormat", 21)
	
	//Table Properties
	//app.updateSetting("controlSize", [value: "Normal", type: "enum"])
	app.updateSetting("controlSize", [value: "15", type: "enum"])
	app.updateSetting("thp", "5")
	app.updateSetting("tvp", "3")
	app.updateSetting("ha", [value: "Stretch", type: "enum"])
	app.updateSetting("va", [value: "Center", type: "enum"])
	
	// Column Properties
	app.updateSetting("column2Header", "Icon")
	app.updateSetting("column3Header", "Name")
	app.updateSetting("column4Header", "State")
	app.updateSetting("column5Header", "Control A/B")
	app.updateSetting("column6Header", "Control C")
		
	//Info Column Properties
	app.updateSetting("hideColumn1", false)
	app.updateSetting("hideColumn2", false)
	app.updateSetting("hideColumn3", false)
	app.updateSetting("hideColumn4", false)
	app.updateSetting("hideColumn5", false)
	app.updateSetting("hideColumn6", false)
	
	app.updateSetting("hideColumn7", true)
	app.updateSetting("column7Header", "Last Active")
	app.updateSetting("info1Source", "lastActive")
	app.updateSetting("its1", "80")
	app.updateSetting("ita1", "Center")
	app.updateSetting("hideColumn8", true)
	app.updateSetting("column8Header", "Duration")
	app.updateSetting("info2Source", "lastActiveDuration")
	app.updateSetting("its2", "80")
	app.updateSetting("ita2", "Center")
	app.updateSetting("hideColumn9", true)
	app.updateSetting("column9Header", "Room")
	app.updateSetting("info3Source", "roomName")
	app.updateSetting("its3", "80")
	app.updateSetting("ita3", "Center")
		
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
	app.updateSetting("column3Header", "Name")
    
	//Row Properties
	app.updateSetting("rts", "90")
	app.updateSetting("rtc", [value: "#000000", type: "color"])
    app.updateSetting("rbc", [value: "#D9ECB1", type: "color"])
	app.updateSetting("rbs", [value: "#FFFFC2", type: "color"])
	app.updateSetting("rbo", "1")
	    
    //Publishing
	app.updateSetting("mySelectedRemote", "")
    app.updateSetting("publishEndpoints", [value: "Local", type: "enum"])
    app.updateSetting("eventTimeout", "2000")
    
    //Set initial Log settings
	app.updateSetting('isLogConnections', false)
	app.updateSetting('isLogActions', true)
	app.updateSetting('isLogPublish', false)
	app.updateSetting('isLogDeviceInfo', false)
	app.updateSetting('isLogError', true)
    app.updateSetting('isLogDebug', false)
    app.updateSetting('isLogTrace', false)
    
	//Have all the sections collapsed to begin with except devices
    state.hidden = [Device: false, Endpoints: true, Polling: true, Design: false, Publish: false]
    state.updatedSessionList = []
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
    <title>🪄#BrowserTitle#</title>
	//This is a placeholder for the link to the Google Materials Font. In its normal state it gets cleaned up with the comments because of the presence of the //.
	#MaterialsFont#
	
<style>
	:root {--control: #controlSize#px; 
			--tickMarks : repeating-linear-gradient(to right, transparent 0%, transparent 4%, black 5%, transparent 6%, transparent 9%);
			//--blinds : repeating-linear-gradient(to right, transparent 0%, transparent 2%, black 2%, #ccc 4%, transparent 4%, transparent 6%, black 6%, #ccc 8%, transparent 8%), linear-gradient( to left, #FFF 0%, #FFF 30%, #111 100%);
			--blinds : repeating-linear-gradient(to right, black 0%, #ccc 3%, black 3%, #ccc 6%, black 6%, #ccc 9%);
			--shades : linear-gradient( 10deg, #000 0%, #333 45%, #CCC 55%, #FFF 100%);
			--dimmer : linear-gradient(to right, #000 0%, #333 15%, #666 30%, #888 45%, #AAA 60%, #DDD 75%, #FFF 100% ); 
			--CT : linear-gradient(to right, #FF4500 0%, #FFA500 16%, #FFD700 33%, #FFFACD 49%, #FFFFE0 60%, #F5F5F5 66%, #FFF 80%,	#ADD8E6 100% ); }  

	html, body { display: flex; flex-direction: column; justify-content: center; align-items: stretch; height: 100%; margin: 5px; font-family: 'Arial', 'Helvetica', sans-serif; cursor: auto;}
	.container { width: 100%; max-width:#maxWidth#px;  margin: 20px auto;}

	/* Mobile Styles - For screens 768px or smaller. For Firefox scrollbar-width: none; eliminates scrollbars within the Dashboard */
	@media (max-width: 768px) { 
		.container { width: 95%; max-width: 100%; padding: 5px; margin-bottom: 20px; } 
		html, body { justify-content: flex-start; align-items: stretch; scrollbar-width: none; overflow: hidden; } 
		}

	/* Eliminates scrollbars within the dashboard for Webkit browsers */
	::-webkit-scrollbar {display: none;}

	/* Suppress the defaults for sliders on each browser platform */
	input[type=range] { -webkit-appearance: none; width: 100%; background: transparent; }
	
	/* Special styling for WebKit/Blink */
	input[type=range]::-webkit-slider-thumb { -webkit-appearance: none; height: var(--control); width: var(--control); border-radius: calc(var(--control) / 2); background: #1E90FF; cursor: pointer; vertical-align:middle;}
	
	/* Apply an outline when the slider is focused (clicked or selected) */
	input[type=range]:focus {outline: 2px solid #FF4500; }
	input[type=range]::-ms-track { width: 100%; cursor: pointer; background: transparent; border-color: transparent; color: transparent;}

	/* All the same stuff for Firefox and IE */
	input[type=range]::-moz-range-thumb, input[type=range]::-ms-thumb { height: 36px; width: 16px; border-radius: 3px; background: #1E90FF; cursor: pointer;}
	
	/* START OF TITLE CLASSES */
	.title{ padding: #tp#px; text-align: #ta#; font-size: #ts#%; font-weight: 400; color: #tc#; background-color: #tb#; display: #titleDisplay#;}
			
	/*  START OF TABLE CLASSES */
	table {width: 100%; border-collapse: collapse; table-layout: auto; }
	th, td { padding: calc(#tvp#px + 3px) #thp#px; text-align: center; vertical-align: middle; border: 1px solid black; transition: background-color 0.3s; user-select:none;}
		
	th:nth-child(1), td:nth-child(1) { width: 6%; display:#hideColumn1#; }
	th:nth-child(2), td:nth-child(2) { display:#hideColumn2#; }
	th:nth-child(3) { padding-left:calc(#thp#px + 5px); text-align:left; width:auto;}, td:nth-child(3) {text-align:left; padding-left:calc(#thp#px); }
	th:nth-child(4), td:nth-child(4) { display:#hideColumn4#; }
	th:nth-child(5), td:nth-child(5) { display:#hideColumn5#; }
	th:nth-child(5), td:nth-child(5) { display:#hideColumn6#; }
	th:nth-child(7), td:nth-child(7) { display:#hideColumn7#; }
	th:nth-child(8), td:nth-child(8) { display:#hideColumn8#; }
	th:nth-child(9), td:nth-child(9) { display:#hideColumn9#; }

	th { background-color: #hbc#; font-weight: bold; font-size: #hts#%; color: #htc#; margin:1px; }
	th.clicked { text-decoration: underline; text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.5); }
	tr { background-color: #rbc#;}
	tr:hover { background-color: #rbs#; }
	.selected-row {	background-color: #rbs#;}

	/* START OF CONTROLS CLASSES */			
	/* Column 1 Checkboxes */
	input[type="checkbox"] {height:var(--control); width:var(--control); margin:0px; margin-top:3px; cursor: pointer; }

	/* Column 2 - Materials Symbols - Icons */
	.material-symbols-outlined {padding:3px; border-radius: 50%; font-size:calc(var(--control) * 1.5 )}
	.material-symbols-outlined.on { background-color:rgba(255,255,0, 0.3); color: #333333;}
	.material-symbols-outlined.off {color: #AAA;}
		
	/* Column 3 Device Names */
	.editable-input {border: none; width: 95%;background: transparent; font-size: #rts#%; color: #rtc#;}
		
	/* Column 4 On/Off Switch */
	.toggle-switch { position: relative; display: inline-block; vertical-align: middle; margin-top: calc(var(--control) / 3); margin-bottom: calc(var(--control) / 5 ); width: calc(var(--control) * 2); height: var(--control); background-color: #CCC; cursor: pointer; border-radius: calc(var(--control) / 2); transition: background-color 0.3s ease; box-shadow: 0 0 calc(var(--control) / 1.5) 0px rgba(255, 99, 71, 1); }
	.toggle-switch::before { content: ''; position: absolute; width: calc(var(--control) * 0.87); height: calc(var(--control) * 0.87); border-radius: 50%; background-color: white; top: calc(var(--control) * 0.066); left: calc(var(--control) * 0.066); transition: transform 0.3s ease; }
	.toggle-switch.on { background-color: #2196F3; box-shadow: 0 0 calc(var(--control) / 1.5) calc(var(--control) / 5) rgba(255, 255, 0, 1); }
	.toggle-switch.on::before { transform: translateX(calc(var(--control))); }

	/* Column 5 - Control Group 1 - Level and Kelvin Sliders */
	.control-container {display:flex;position: relative; width: 95%; display: flex; justify-content: center; align-items: center; background-color:#rbc#; margin:auto; }
	.CT-slider, .level-slider, .blinds-slider, .shades-slider, .volume-slider, .tilt-slider { width: 90%; opacity:0.75; border-radius:0px; height:var(--control); outline: 2px solid #888; cursor: pointer;}
	.CT-value, .level-value, .blinds-value, .shades-value, .volume-value, .tilt-value {position:absolute; top:50%; transform:translateY(-50%); font-size:90%; pointer-events:none; text-align:center; cursor:pointer; font-weight:bold; background:#fff8; padding:2px;}

	/* Custom properties for WebKit-based browsers (Chrome, Safari) */
	.CT-slider::-webkit-slider-runnable-track { background: var(--CT); height: 100% }
	.level-slider::-webkit-slider-runnable-track { background: var(--dimmer); height: 100%}	
	.blinds-slider::-webkit-slider-runnable-track { background: var(--blinds); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.shades-slider::-webkit-slider-runnable-track { background: var(--shades); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }

	//This creates a series of tick marks representing 0 - 100 with a color gradient background from green to red as volume increases.
	.volume-slider::-webkit-slider-runnable-track { background: var(--tickMarks), linear-gradient(to right, green, orange, red); height: 100%; background-size: 100% 100%; background-repeat: no-repeat; border: 0px solid #DDD;}
	.tilt-slider::-webkit-slider-runnable-track { background: var(--tickMarks),linear-gradient(to right, #000 0%, #333 10%, #0D47A1 30%, #17D 40%, #4682B4 50%, #8CF 65%, #FFF 85%, #FFF 100%); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.level-slider::-webkit-slider-thumb, .CT-slider::-webkit-slider-thumb, .shades-slider::-webkit-slider-thumb, .blinds-slider::-webkit-slider-thumb, .volume-slider::-webkit-slider-thumb { background: dodgerblue; border-radius: 50%; cursor: pointer; }   
	.tilt-indicator {font-size: var(--control); line-height: var(--control); transform-origin: center center; transition: transform 0.1s ease-in-out;}
	
	/* Add Mozilla Browser Support - Untested */
	/* Custom properties for Mozilla Firefox */
	.CT-slider::-moz-range-track { background: var(--CT); height: 100%; }
	.level-slider::-moz-range-track { background: var(--dimmer); height: 100%; }   
	.blinds-slider::-moz-range-track { background: var(--blinds); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.shades-slider::-moz-range-track { background: var(--shades); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.volume-slider::-moz-range-track { background: var(--tickMarks), linear-gradient(to right, green, orange, red); height: 100%; background-size: 100% 100%; background-repeat: no-repeat; border: 0px solid #DDD; }
	.tilt-slider::-moz-range-track { background: var(--tickMarks), linear-gradient(to right, #000 0%, #333 10%, #0D47A1 30%, #17D 40%, #4682B4 50%, #8CF 65%, #FFF 85%, #FFF 100%); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.level-slider::-moz-range-thumb, .CT-slider::-moz-range-thumb, .shades-slider::-moz-range-thumb, .blinds-slider::-moz-range-thumb, .volume-slider::-moz-range-thumb { background: dodgerblue; border-radius: 50%; cursor: pointer; }
	
	/* Column 6 - Control Group 2 - Color */
	input[type="color"]::-webkit-color-swatch-wrapper { padding: 0px; }
	.colorPicker{border: 2px solid #888; border-radius: 2px; width: calc(var(--control) * 3); height: calc( var(--control) * 1.25); cursor: pointer;}	/* Column 6 Color Control */

	/* Info Columns 7 & 8 & 9 */
	.info1, .info2, .info3 { border: none; background: transparent; color: #rtc#; white-space: nowrap;}
	.info1 { font-size: #its1#%; text-align: #ita1#; }
	.info2 { font-size: #its2#%; text-align: #ita2#; }
	.info3 { font-size: #its3#%; text-align: #ita3#; }

	/* Define glow effects */	
	.glow-EffectSuccess {outline: #pollUpdateWidth#px solid #pollUpdateColorSuccess#;}
	.glow-EffectFail {outline: #pollUpdateWidth#px solid #pollUpdateColorFail#;}
	.glow-EffectCT {outline: 2px solid #1E90FF;}
	.glow-EffectRGB {border: 2px solid #1E90FF;}
		
	/* Refresh bar styling */
	#shuttle { display:none; position:relative; height:#shuttleHeight#px; width:5%; background-color:#shuttleColor#; border-radius:3px; animation:none;}

	@keyframes slide { 0% { left: 0%; width: 5%; } 50% { left: 95%; width: 5%; } 100% { left: 0%; width: 5%; } }
	@keyframes slideBackward { 0% { left: 95%; width: 5%; } 100% { left: 0%; width: 5%; } }
	@keyframes blink { 0% { opacity: 1; } 50% {opacity: 0;} 100% {opacity: 1;} }
	.blinking { animation: blink 1s infinite; }

	.button-group { display: flex; justify-content: space-between; align-items: center; margin: 0 auto; text-align: center;}
	.button { max-height: var(--control); padding: 5px 10px; margin: 0 3px; border-radius: 10px; background-color: #d3d3d3; text-align: center; transition: background-color 0.3s, border-color 0.3s, outline 0.3s; 
			display: flex; align-items: center; justify-content: center; color: #FFF; font-size: calc(5 + var(--control) / 1.5); cursor: pointer; }
	.button:hover { background-color: #62B4FF; outline: 3px solid #FFA500; }
	.button:active { background-color: #1C86EE; outline: 3px solid #1E90FF; }
	.button.selected { background-color: #1E90FF; outline: 3px solid #1E90FF; }
	.button-group.disabled { opacity: 0.75; pointer-events: none; cursor: not-allowed; }

	@keyframes spin { from {transform: rotate(0deg);} to {transform: rotate(360deg);} }
	.spin-low {animation: spin 3s linear infinite;}
	.spin-medium {animation: spin 1.5s linear infinite;}
	.spin-high {animation: spin 0.75s linear infinite;}

</style>
</head>

//***********************************************  HTML Block  *************************************************************************
//**************************************************************************************************************************************

<body>
<div class="container">
	<div class="title" id="title">#tt#</div>
	<table id="sortableTable">
		<thead>
			<tr>
				<th><input type="checkbox" id="masterCheckbox" onclick="toggleAllCheckboxes(this)" onchange="updateHUB()" title="Select All/Deselect All"></th>
				<th id="icon" class="sortLinks" onclick="sortTable(1, this);" title="Icon - Sort">Icon</th>
				<th id="nameHeader" class="sortLinks" onclick="sortTable(2, this);">
    				<span title="Sort Name A-Z">#column3Header#</span>
    				<span style="float: right; font-size:inherit;" onclick="event.stopPropagation(); refreshPage(50);" title="Refresh Data">↻ </span>
				</th>
				<th id="stateHeader" class="sortLinks th" onclick="sortTable(3, this);"><span title="Sort State On-Off">State</span></th>
				<th id="ControlAB" class="sortLinks" onclick="toggleControl()" title="Toggle Control A/B">#column5Header#</th>
				<th id="color">#column6Header#</th> 
				<th id="info1" class="sortLinks" onclick="sortTable(6, this);" title="Info1 - Alpha Sort">#Info1#</th>
				<th id="info2" class="sortLinks" onclick="sortTable(7, this);" title="Info2 - Alpha Sort">#Info2#</th>
				<th id="info3" class="sortLinks" onclick="sortTable(8, this);" title="Info3 - Alpha Sort">#Info3#</th>
			</tr>
		</thead>
		<tbody><!-- Table rows will be dynamically loaded from JSON --></tbody>
	</table>
	<div id="shuttle"><br></div>
</div>
<script>
														  
// Retrieve values from sessionStorage or initialize to default for the active column and direction
// Session storage does no persist across the closing of the browser window. localStorage does persist across the closing of the browser.
let storedSortDirection = JSON.parse(localStorage.getItem("sortDirection"));
let sortDirection = storedSortDirection || { activeColumn: 2, direction: 'asc' };

let showSlider = (localStorage.getItem("showSlider") === "A" || localStorage.getItem("showSlider") === "B") ? localStorage.getItem("showSlider") : "A";
let lastCommand = "None";

sessionStorage.removeItem('sessionID');
let sessionID = sessionStorage.getItem('sessionID') || (sessionStorage.setItem('sessionID', (Math.abs(Math.random() * 0x7FFFFFFF | 0)).toString(16).padStart(8, '0')), sessionStorage.getItem('sessionID'));
let isLogging = sessionStorage.getItem('isLogging') === 'true' ? true : false;
isLogging = true;

//Polling Related variables
let pollInterval = #pollInterval#;
let isPollingEnabled = #isPollingEnabled#;
if (isPollingEnabled === true ) { 
	const poller = startPolling('#URL1#', pollResult); 
	//Start the Shuttle animation
	const shuttle = document.getElementById('shuttle');
	shuttle.style.display = 'block';
	shuttle.style.animation = `slide ${pollInterval *2}ms ease-in-out infinite`;
}

// Force Enable or Disable logging
sessionStorage.setItem('isLogging', 'false');

//This has to do with transaction handling
let transactionTimeout = #commandTimeout#;
let transaction = null;


//***********************************************  Table Body  *************************************************************************
//**************************************************************************************************************************************

//Updates the Table with the JSON received from the Hub.
function loadTableFromJSON(myJSON) {
    const tbody = document.querySelector("#sortableTable tbody");
    tbody.innerHTML = ""; // Clear existing table rows
	const savedCheckboxStates = JSON.parse(sessionStorage.getItem("checkboxStates")) || {};

    myJSON.forEach((item, index) => {
        const row = document.createElement('tr');

		let control1HTML = "";	//Holds HTML for the first controls column
		let control2HTML = "";   //Holds HTML for the second controls column

		// Data mapping for row dataset
		const dataMap = { dID: item.dID, dType: item.dType, speed: item.speed, position: item.position, tilt: item.tilt, volume: item.volume, colorMode: item.colorMode || "None", info1: item.info1, info2: item.info2, info3: item.info3 };

		// Apply each property to the row dataset using a loop and map
		Object.entries(dataMap).forEach(([key, value]) => { row.dataset[key] = value; });

        // Ensure color is defined and in the correct format
        const colorValue = item.color && /^#[0-9A-F]{6}$/i.test(item.color) ? item.color : "#FFF";
		
		//Configure the Icon text.
		let iconText = (item.switch === 'on' || item.switch === 'off') ? `<i class='material-symbols-outlined ${item.switch === 'on' ? 'on' : 'off'}'>${item.icon}</i>` : `<i class='material-symbols-outlined blinking'>${item.icon}</i>`;

	     // Sliders for dType <= 5 which is all kinds of bulbs and switches. 
		if (item.dType <= 5 ){
			control1HTML = `<div class="control-container">
               	<input type="range" class="level-slider" min="0" max="100" value="${item.level}"
                style="display: ${showSlider === 'A' && [2, 3, 4, 5].includes(item.dType) ? 'block' : 'none'}"
                oninput="updateSliderValue(this, 'level')" onchange="updateHUB()">
             	<span class="level-value" style="display: ${showSlider === 'A' ? 'block' : 'none'}">${item.level}%</span>

				<input type="range" class="CT-slider ${item.colorMode === 'CT' ? 'glow-EffectCT' : ''}" min="2000" max="6500" value="${item.CT}"
       			style="display: ${showSlider === 'B' && [3,5].includes(item.dType) ? 'block' : 'none'}"
       			oninput="updateSliderValue(this, 'CT')" onchange="updateHUB()">
				<span class="CT-value" style="color:black; display: ${showSlider === 'B' && [3,5].includes(item.dType) ? 'block' : 'none'}">${item.CT}°K</span>
            </div>`;
		};

		// dTypes 10, 11 and 13 are a valve, lock and Garage Door Controller respectively and are treated as simple switches.
		iconText = (item.switch === 'on' || item.switch === 'off') ? `<i class='material-symbols-outlined ${item.switch === 'on' ? 'on' : 'off'}'>${item.icon}</i>` : `<i class='material-symbols-outlined blinking'>${item.icon}</i>`;

		// Buttons for Fan (Radio buttons did not size properly)
		if (item.dType === 12) {
			let spinClass = '';
			if (item.dType === 12 && item.switch === 'on') 
				{ spinClass = item.speed === 'low' ? 'spin-low' : item.speed === 'medium' ? 'spin-medium' : item.speed === 'high' ? 'spin-high' : ''; } 
			
			iconText = (item.switch === 'on' || item.switch === 'off') ? `<i class='material-symbols-outlined ${item.switch === 'on' ? 'on' : 'off'} ${spinClass}'>${item.icon}</i>` : `<i class='material-symbols-outlined blinking'>${item.icon}</i>`;
    		const isDisabled = item.switch === 'off' ? 'disabled' : ''; // Determine if buttons should be disabled
    		const disabledClass = item.switch === 'off' ? 'disabled' : ''; // Add a visual class for the disabled state for the whole radio group
			control1HTML = `<div class="button-group ${disabledClass}">
        					<div class="button ${item.speed === 'low' ? 'selected' : ''} ${isDisabled}" data-speed="low" onclick="speed(this); updateHUB(); toggleChecked(this)">Low</div>
        					<div class="button ${item.speed === 'medium' ? 'selected' : ''} ${isDisabled}" data-speed="medium" onclick="speed(this); updateHUB(); toggleChecked(this)">Med</div>
        					<div class="button ${item.speed === 'high' ? 'selected' : ''} ${isDisabled}" data-speed="high" onclick="speed(this); updateHUB(); toggleChecked(this)">High</div></div>
			`};

		//Garage Door
		if (item.dType === 13){ iconText = (item.door === 'open' || item.door === 'closed' ) ? `<i class='material-symbols-outlined ${item.switch === 'on' ? 'on' : 'off'}'>${item.icon}</i>` : `<i class='material-symbols-outlined blinking'>${item.icon}</i>`; };

		//Shade
		if (item.dType === 14){
			iconText = (item.windowShade === 'open' || item.windowShade === 'closed' || item.windowShade === 'partially open') ? `<i class='material-symbols-outlined ${item.switch === 'on' ? 'on' : 'off'}'>${item.icon}</i>` : `<i class='material-symbols-outlined blinking'>${item.icon}</i>`;
			control1HTML = `<div class="control-container"><input type="range" class="shades-slider" min="0" max="100" value="${item.position}" oninput="updateSliderValue(this, 'position')" onchange="updateHUB()">
                			<span class="shades-value" style="display: ${showSlider === 'A' ? 'block' : 'none'}"><b>${item.position}%</b></span></div>`;
		};

		//Blind
		if (item.dType === 15){
			iconText = (item.windowShade === 'open' || item.windowShade === 'closed' || item.windowShade === 'partially open') ? `<i class='material-symbols-outlined ${item.switch === 'on' ? 'on' : 'off'}'>${item.icon}</i>` : `<i class='material-symbols-outlined blinking'>${item.icon}</i>`;
			control1HTML = `<div class="control-container"><input type="range" class="blinds-slider" min="0" max="100" value="${item.position}" oninput="updateSliderValue(this, 'position')" onchange="updateHUB()">
                			<span class="blinds-value" style="display: ${showSlider === 'A' ? 'block' : 'none'}"><b>${item.position}%</b></span></div>`;

			control2HTML = `<div style="display: flex; align-items: center;"> <div class="control-container" style="display: flex; align-items: center;">
      		<input type="range" class="tilt-slider" min="0" max="90" value="${item.tilt}" oninput="updateSliderValue(this, 'tilt')" onchange="updateHUB()"> 
      		<span class="tilt-value" style=" display: ${showSlider === 'A' ? 'block' : 'none'}">${item.tilt}°</span>
    		</div><div id="tilt-indicator" class="tilt-indicator" style="margin-left: 20px; margin-right: 10px; display: inline-block; vertical-align: middle;"> | </div></div>`;
    	};   
		
		//Volume
		if (item.dType === 16 ){
			control1HTML = `<div class="control-container">
               	<input type="range" class="volume-slider" min="0" max="100" value="${item.volume}"
                oninput="updateSliderValue(this, 'volume') " onchange="updateHUB()">
             	<span class="volume-value" style="display: ${showSlider === 'A' ? 'block' : 'none'}">${item.volume}%</span>
            </div>`;
		};

		// Control 2 - Color Picker
		if (item.dType === 4 || item.dType === 5) { control2HTML = `<input type="color" class="colorPicker ${item.colorMode === 'RGB' ? 'glow-EffectRGB' : ''}" id="colorInput${index}" value="${colorValue}" onchange="updateColor(this); updateHUB()">` }

		//No controls other than the switch for some devices; switch, valve, lock and garage door,
		if (item.dType === 1 || item.dType === 10 || item.dType === 11 || item.dType === 13 ) control1HTML = '';
						
        const isChecked = savedCheckboxStates[item.dID] || false;
           
		row.innerHTML = `
			<td><input type="checkbox" class="option-checkbox" ${isChecked ? 'checked' : ''} onchange="toggleRowSelection(this); updateHUB()"></td>
            <td>${iconText}</td>
            <td><input type="text" class="editable-input" value="${item.name}" onchange="updateHUB()" readonly></td>
            <td><div class="toggle-switch ${item.switch === 'on' ? 'on' : ''}" data-state="${item.switch}" onclick="toggleSwitch(this); updateHUB()"></div></td>
            <td>${control1HTML}</td>
            <td>${control2HTML}</td>
            <td><div class="info1">${item.info1}</div></td>
            <td><div class="info2">${item.info2}</div></td>
			<td><div class="info3">${item.info3}</div></td>`;
        tbody.appendChild(row);

    });
	underlineColumnHeader();
	updateAllTiltIndicators();
}

//***********************************************  Data Transfer ***********************************************************************
//**************************************************************************************************************************************

//Update the Tilt angle on ALL tilt indicators
function updateAllTiltIndicators() {
    // Select all rows in the table then iterate through each row
    const rows = document.querySelectorAll("#sortableTable tbody tr");
    rows.forEach((row, index) => {
        // Find the tilt slider and tilt indicator in the row
        const tiltSlider = row.querySelector(".tilt-slider");
        const tiltIndicator = row.querySelector(".tilt-indicator");

        if (!tiltSlider  || !tiltIndicator) return;
	    tiltIndicator.style.transform = `rotate(${-tiltSlider.value}deg)`; // Counterclockwise rotation
    });
}

// Gather all table data into a JSON output
function updateHUB() {
    const rows = document.querySelectorAll("#sortableTable tbody tr");
    const output = Array.from(rows).map((row) => {
        const dType = Number(row.dataset.dType);
        const outputData = { name: row.querySelector('td:nth-child(3) input').value, dID: row.dataset.dID, dType, };

        // Conditionally add fields based on dType and lastCommand
        const addField = (condition, field, value) => condition && (outputData[field] = value);
        addField(dType >= 1 && lastCommand === "switch", "switch", row.querySelector('.toggle-switch').dataset.state);
        addField( (dType >= 2 && dType <= 5 ) && lastCommand === "level", "level", parseInt(row.querySelector('.level-slider')?.value || 0));
        addField( (dType === 3 || dType === 5) && lastCommand === "CT", "CT", parseInt(row.querySelector('.CT-slider')?.value || 0));
        addField( (dType === 4 || dType === 5) && lastCommand === "color", "color", (row.querySelector('input[type="color"]')?.value || '#000000').toUpperCase());
		addField( dType === 12 && lastCommand === "speed", "speed", row.querySelector('.button.selected') ? row.querySelector('.button.selected').getAttribute('data-speed') : "off" );
		addField( (dType === 14 ) && lastCommand === "position", "position", parseInt(row.querySelector('.shades-slider')?.value || 0));
		addField( (dType === 15 ) && lastCommand === "position", "position", parseInt(row.querySelector('.blinds-slider')?.value || 0));
		addField( (dType === 15 ) && lastCommand === "tilt", "tilt", parseInt(row.querySelector('.tilt-slider')?.value || 0));
		addField( (dType === 16 ) && lastCommand === "volume", "volume", parseInt(row.querySelector('.volume-slider')?.value || 0));
        return outputData;
    });
	if (isLogging) console.log("Output is:", output)
    // Send data to the backend
    sendData(JSON.stringify(output));
}

//Sends the Data to the Hub
function sendData(payload) {
	handleTransaction("begin");
    const url = '#URL#';
    fetch(`${url}&sessionID=${sessionID}`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: payload }).catch(error => {
        console.error('Error:', error);
    });
}

// Function to fetch JSON data from a URL and return it
async function fetchData() {
	if (isLogging) { console.log("fetchData(): Downloading data from Hub.") };
	const url = '#URL#'; // Example URL
	try {
		const response = await fetch(`${url}&sessionID=${sessionID}`);	//Pass the session ID back to the Hub app to track state.
		const jsonData = await response.json(); // Parse the response as JSON
		return jsonData; // Return the JSON data
	} catch (error) {
		console.error("Error fetching data:", error);
		return null; // Return null if there's an error
	}
}

//***********************************************  onUpdate event handling   ***********************************************************
//**************************************************************************************************************************************

// Fan Speed change - Highlight the selected button
function speed(button) {
    const row = button.closest('tr');
    row.querySelectorAll('.button').forEach(btn => btn.classList.remove('selected'));
    button.classList.add('selected');
    lastCommand = "speed";
}

// Update the color value based on the input
function updateColor(colorInput) {
    const row = colorInput.closest('tr');
    const checkbox = row?.querySelector("input[type='checkbox']");

    // Update the current row's color display
    const colorDisplay = row?.querySelector('.color-display'); // Assuming there's an element to show the color
	//Set the color of this instance of the control
    if (colorDisplay) { colorDisplay.style.backgroundColor = colorInput.value; }

    // Sync the colors for other checked rows to this value
    if ( checkbox && checkbox.checked) { syncRows("color", colorInput.value); }

    lastCommand = "color"; // Track the last command
}

//Update the slider text label showing the selected value for level, position or CT
function updateSliderValue(slider, command) {
    const row = slider?.closest('tr');
	const checkbox = row?.querySelector("input[type='checkbox']");

    // Check if the slider is of type slider-tilt and update accordingly
    let valueText = `${slider.value}%`; // Default value for percentage
	if (slider.classList.contains('tilt-slider')) { valueText = `${slider.value}°`}; 
    
	// Change the slider value for this row
    slider.nextElementSibling.innerText = valueText;
	if (command === "CT") row.dataset.colorMode = "CT";
	if (command === "tilt" ) updateAllTiltIndicators();
	
	//Sync the sliders to this value
	if ( checkbox && checkbox.checked) {    	
		//Now change the label for the other selected rows
        syncRows(command, slider.value);
    }
    lastCommand = command;
}

// Toggle the first control column between Control A and Control B sliders and hide unused sliders
function toggleControl() {
    // Toggle the Control Group
    showSlider = showSlider === "A" ? "B" : "A";
    sessionStorage.setItem("showSlider", showSlider);

    // Update visibility for sliders and values
    document.querySelectorAll('tr').forEach(row => {
        const dType = Number(row.dataset.dType);

        // Determine visibility for each slider type
        const showLevel = showSlider === "A" && !(dType <= 1 || dType >= 10);
        const showCT = showSlider === "B" && (dType === 3 || dType === 4 || dType === 5 || dType >= 10);

        // Update visibility for Level sliders and values
        row.querySelectorAll('.level-slider, .level-value').forEach(el => { el.style.display = showLevel ? 'block' : 'none'; });

        // Update visibility for CT sliders and values
        row.querySelectorAll('.CT-slider, .CT-value').forEach(el => { el.style.display = showCT ? 'block' : 'none'; });
    });
}


//***********************************************  Table UI Management  ****************************************************************
//**************************************************************************************************************************************


//Underline the last active column header
function underlineColumnHeader() {
    const headers = document.querySelectorAll('#sortableTable thead th');
    
    // Remove the 'clicked' class from all headers
    headers.forEach(header => { header.classList.remove('clicked'); });

    // Add the 'clicked' class to the active header (the one that was clicked)
    if (sortDirection.activeColumn >= 0 && sortDirection.activeColumn < headers.length) {
        headers[sortDirection.activeColumn].classList.add('clicked');
    } else { console.error('Invalid active column index'); }
}

//***********************************************  CheckBox Handling   *****************************************************************
//**************************************************************************************************************************************

function toggleRowSelection(checkbox) {
    const row = checkbox.closest('tr');
    const dID = row.dataset.dID;
    const checkboxStates = JSON.parse(sessionStorage.getItem("checkboxStates")) || {};    
    row.classList.toggle('selected-row', checkbox.checked);
    if (dID) checkboxStates[dID] = checkbox.checked;
    sessionStorage.setItem("checkboxStates", JSON.stringify(checkboxStates));
}

function toggleAllCheckboxes(masterCheckbox) {
    document.querySelectorAll('.option-checkbox').forEach(checkbox => {
        checkbox.checked = masterCheckbox.checked;
        toggleRowSelection(checkbox);
    });
}

function toggleChecked(label) {
	// Remove 'checked' class from all labels
	const labels = document.querySelectorAll('.radio-label');
	labels.forEach(lbl => lbl.classList.remove('checked'));
	// Add 'checked' class to the clicked label
	label.classList.add('checked');
}

//Update the switches
function toggleSwitch(switchElement) {
    const row = switchElement.closest('tr');
    const newState = (switchElement.classList.toggle('on') ? 'on' : 'off');
    switchElement.dataset.state = newState;

    row?.querySelector('.radio-group')?.classList.toggle('disabled', newState === 'off');
    row?.querySelectorAll('.radio-group input[type="radio"]').forEach(input => input.disabled = newState === 'off');

    if (row?.querySelector("input[type='checkbox']")?.checked) syncRows("switch", newState);
    lastCommand = "switch";
}

//Takes any action initiated by the user and applies it to any other row that has the master checkbox checked.
function syncRows(command, value) {
	//console.log ("Received: ", command);

    document.querySelectorAll('.option-checkbox:checked').forEach(checkbox => {
        const row = checkbox.closest('tr');
        const updateElement = (selector, value) => {
            const element = row.querySelector(selector);
            if (element) element.value = value;
        };
        switch (command) {
            case "color":
                updateElement('input[type="color"]', value);
                row.dataset.colorMode = "RGB";
                break;
            case "level":
				if (row.querySelector('.level-slider')) { // Ensure the slider exists
     				updateElement('.level-slider', value);
        			row.querySelector('.level-value').innerText = value + '%';
    			};
				break;
			case "CT":
				if (row.querySelector('.CT-slider')) { // Ensure the slider exists
					updateElement('.CT-slider', value);
                	row.querySelector('.CT-value').innerText = value + '°K';
    			    }
				break;
			case "tilt":
				if (row.querySelector('.tilt-slider')) { // Ensure the slider exists
     				updateElement('.tilt-slider', value);
        			row.querySelector('.tilt-value').innerText = value + '%';
    			};
                break;
			case "position":
				if (row.querySelector('.shades-slider')) { // Ensure the slider exists
      				updateElement('.shades-slider', value);
        			row.querySelector('.shades-value').innerText = value + '%';
    			};
				if (row.querySelector('.blinds-slider')) { // Ensure the slider exists
      				updateElement('.blinds-slider', value);
        			row.querySelector('.blinds-value').innerText = value + '%';
    			};
				break;
			case "volume":
				if (row.querySelector('.volume-slider')) { // Ensure the slider exists
      				updateElement('.volume-slider', value);
        			row.querySelector('.volume-value').innerText = value + '%';
    			};
				break;
            case "switch":
                const toggleSwitch = row.querySelector('.toggle-switch');
                if (toggleSwitch) {
                    toggleSwitch.classList.toggle('on', value === 'on');
                    toggleSwitch.dataset.state = value;
                }
                break;
        }
    });
    lastCommand = "checked";
}




//***********************************************  Polling and Transactions  ***********************************************************
//**************************************************************************************************************************************

//Starts the polling process of the Hub using the global value of pollInterval which can vary between the minimum and maximum values depending on activity.
function startPolling(url, pollResult) {
    const poller = setInterval(async () => {
        try {
            const response = await fetch(`${url}&sessionID=${sessionID}`);
            if (!response.ok) throw new Error(`Error: ${response.status}`);
            const data = await response.json();
            pollResult(data);

            if (isLogging) console.log("Session ID:", sessionID);
        } catch (error) {
            console.error("Polling error:", error);
            clearInterval(poller);
        }
    }, pollInterval);
    return poller;
}

// This is the callback function. When the polling process receives a response, it comes here and we check if there is an update pending or not.
function pollResult(data) {
    if (data.update) {
        if (isLogging) console.log("Update is: True");
        // We have an update, so mark the transaction as complete
        handleTransaction("end");
        initialize();
        const table = document.querySelector("table");
        table.classList.add('glow-EffectSuccess');
        setTimeout(() => table.classList.remove('glow-EffectSuccess'), #pollUpdateDuration#); 
    } else {
        if (isLogging) console.log("Update is: False");
        handleTransaction("check");
    }
}

function handleTransaction(action) {
    switch (action) {
        case "begin":
            transaction = Date.now(); // Start the transaction
            if (isLogging) console.log("Transaction started:", transaction);
            break;

        case "end":
            transaction = null; // End the transaction
            if (isLogging) console.log("Transaction finished");
            break;

        case "check":
            if (!transaction) {
                if (isLogging) console.log("No active transaction to check");
                return;
            }

            const elapsedTime = Date.now() - transaction;
            if (isLogging) console.log("Elapsed time is:", elapsedTime);

            if (elapsedTime > transactionTimeout) {
                console.log("Transaction is late");
                const table = document.querySelector("table");
                table.classList.add('glow-EffectFail');

                setTimeout(() => {
                    handleTransaction("end"); // End the transaction
                    table.classList.remove('glow-EffectFail');
                    if (isLogging) console.log("Glow effect removed!");
                    initialize();
                }, #pollUpdateDuration#);
            } else {
                if (isLogging) console.log("Transaction is running");
            }
            break;
    }
}

//***********************************************  Sorting   ***************************************************************************
//**************************************************************************************************************************************

// Sort table by column with optional secondary sort on Device Name
function sortTable(columnIndex) {
    const tbody = document.querySelector("#sortableTable tbody");
    const rows = Array.from(tbody.rows);

    // Update sort state
    sortDirection.activeColumn = columnIndex === -1 ? sortDirection.activeColumn : columnIndex;
    sortDirection.direction = sortDirection.direction === 'asc' ? 'desc' : 'asc';
    localStorage.setItem("sortDirection", JSON.stringify(sortDirection));
    sessionStorage.setItem("activeColumn", sortDirection.activeColumn);

    const direction = sortDirection.direction === 'asc' ? 1 : -1;

    rows.sort((a, b) => {
        const getCellData = (row, idx) => idx === 3
            ? row.cells[idx]?.querySelector('.toggle-switch')?.dataset.state || ""
            : row.cells[idx]?.querySelector('input')?.value?.toLowerCase() || row.cells[idx]?.textContent?.trim().toLowerCase() || "";

        const primaryA = getCellData(a, columnIndex);
        const primaryB = getCellData(b, columnIndex);
        const secondaryA = getCellData(a, 2);
        const secondaryB = getCellData(b, 2);

        return primaryA !== primaryB
            ? direction * (primaryA > primaryB ? 1 : -1)
            : secondaryA > secondaryB ? 1 : secondaryA < secondaryB ? -1 : 0;
    });

    tbody.append(...rows);
    underlineColumnHeader();
}

//***********************************************  Initialization  and Miscellaneous  **************************************************
//**************************************************************************************************************************************

// Call the function and handle the returned data
function initialize() {
	if (isLogging) { console.log ("Initialize: fetching data") };
	fetchData().then(data => {
		if (data) {
			if (isLogging) { console.log("Fetched JSON Data:", data) };
			loadTableFromJSON(data);
			sortTable(-1 , null);  //-1 Indicates to use the saved sort order, Null - Indicates to use the last Header used.
		} else {
		if (isLogging) { console.log("Failed to fetch data.") };
		}
	});
}

//Performs a complete refresh of the page.
function refreshPage(timeout) {
	setTimeout(function() { location.reload(true);  }, timeout);  // 1000 milliseconds = 1 second
}

function rgb2hex(rgbString) {
    return '#' + rgbString.slice(4, -1).split(',').map(num => 
        ('0' + parseInt(num.trim()).toString(16)).slice(-2)
    ).join('');
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
   

