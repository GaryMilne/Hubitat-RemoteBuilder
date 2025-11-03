/**
*  Remote Builder SmartGrid
*  Version: See ChangeLog
*  Download: See importUrl in definition
*  Description: Used in conjunction with child apps to generate tabular reports on device data and publishes them to a dashboard.
*
*  Copyright 2025 Gary J. Milne  
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.

*  License:
*  You are free to use this software in an un-modified form. Software cannot be modified or redistributed.
*  You may use the code for educational purposes or for use within other applications as long as they are unrelated to the 
*  production of tabular data in HTML form, unless you have the prior consent of the author.
*  You are granted a license to use Remote Builder in its standard configuration without limits.
*  Use of Remote Builder Advanced (which includes SmartGrid) requires a license key that must be issued to you by the original developer. TileBuilderApp@gmail.com
*
*  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
*  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 

*  Authors Notes:
*  For more information on Remote Builder check out these resources.
*  Original posting on Hubitat Community forum: https://community.hubitat.com/t/release-remote-builder-a-new-way-to-control-devices-7-remotes-available/142060
*  Remote Builder Documentation: https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote%20Builder%20Help.pdf
*  Remote Builder - SmartGrid Documentation: https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote_Builder_SmartGrid_Help.pdf
*  Remote Builder - SmartGrid - Hard launch announcement https://community.hubitat.com/t/release-smartgrid-create-grids-of-controls-that-you-can-access-from-anywhere/148457
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
*  Version 3.0.1 - Fix issue with old code being used for the body,html and @media sections affecting rotation. Fix bug with hiding Control C. Fan buttons resize better. Added width option for Control columns.
*  Version 3.0.2 - Improved screen sizing for mobile devices. Added scrollbars when neccessary. Fixed sorting of State column and added directional indicators.
*  Version 3.0.3 - Reduced the amount of data in the packets sent from JS to the Hub. Lookup of device name from the ID is now performed on the Hub.
*  Version 3.0.4 - Improved handling of icons and classes. These are determined on the Hub and sent to the client.
*  Version 3.0.5 - Added support for contacts, leak and temperature along with filtering. Only open contacts or only wet sensors.
*  Version 3.0.6 - Added ability to pin controls. 
*  Version 3.0.7 - Added options and controls for selecting the selectedRow and pinnedRow colors. Combined the Info and Columns\Headers sections.
*  Version 3.0.8 - Re-worked logic for the gathering of information for info columns to eliminate unnecessary calls.
*  Version 3.0.9 - Added missing variables to updateVariables(). Added mid points for tilePreviewWidth. Made all pinned objects remain visible regardless of any filter setting.
*  Version 3.1.0 - Split Devices into Controls and Sensors. Added halfTone for sort column header box. Flipped definition of Lock to open == active.
*  Version 3.1.1 - Added a StorageKey() and AppID to isolate any local or session storage variables between iFrames.
*  Version 3.1.2 - Fixed issue with sorting using State. Sort for state now always use A-Z for the name column as the secondary key. Changed clicked column header to eliminate directional arrows and add a background gradient to indicate direction. Eliminated halftone code.
*  Version 3.1.3 - Switched sort header directional indicators to a user configurable color underline.
*  Version 3.1.4 - Bug with highlight color not being initialized correctly. Added some smaller options to control sizes. Added option to select 0 or 1 decimal places for temperatures. Fixed bug with display of Slider values on A/B switch. Changed Help File link.
*  Version 3.1.5 - Set slider value text color to match row text color. Fixed issue with control columns not being hidden. Added colorTemperature as a possible output for the Info columns. Various minor fixes to issue report in the community forums. 
*  Version 3.1.5B - Fixes issue with one of the the control columns not hiding correctly. Forces a decimal point even for integer values when selecting "1 Decimal Place" to provide more consisten formatting.
*  Version 3.1.6 - Fixes issue with sensor data in the State column not being formatted as per the general text color and size. Changes how column space is allocated and eliminates the need for <colgroup>.
*  Version 3.1.7 - Change how the HTML fills the iframe to avoid unneccessary scrollbars on the dashboard. Add media query so that phones and tables get a near full screen experience.
*  Version 3.1.8 - Added Top-Margin input and modified @media query to use resolution instead of screen size. Modified the html,body settings in order to get things stretched and centered.
*  Version 3.1.9 - Moved the update Status from an outline to just modifying the Table outer border. Also made the shuttle operate on top of the Grid border to reduce vertical space consumption.
*  Version 3.2.0 - Big improvements in the accuracy of the composer window when it comes to reproducing the final dashboard layout, especially when using the default 200px X 190px or multiple thereof. Adds border controls and improves padding options and padding vertical resolution.
*  Version 4.0.0 - Add Modal window to acccess dynamic settings. Added selection boxes to display All Switches, Only ON and Only Off : Enable\Disable Polling : PollInterval. Others may follow.
*  				   Changed the Section layouts into a single Horizontal menu to create more visible space for the "Design SmartGrid" section. Reorganized the SmartGrid formatting menus to be a little easier to use. Added color schemes.
*  				   Enable Drag and Drop Support with Custom Sort. Add Custom Rows. Add variables.
*  Version 4.0.1 - Implement beforeunload function to clean up memory on exit. Remove Selection Boxes for Custom Rows. Pause polling indicator during slider actions. Change shuttle animation to use less CPU. 
*  Version 4.0.2 - Added some logging to the publishRemote function.
*  Version 4.0.3 - Internal Only: Remove checkBoxes for Custom Rows and Sensors. Add A/B Configuration info under Endpoints Help. Add Control C column as option for Text is Custom Rows. 
*  Version 4.0.4 - Remove submitOnChange for all text boxes in Custom Rows and add Apply Changes button for better navigation. Expanded variables to have multiple variables per Source.
*  Version 4.1.0 - Created a separate tab for Device Renaming. Added Hub Properties as Variables. Added Capitalization option for Variable Data and adherence to global Decimal Places. Added formatting for [mark] tag and added optional [m1] tag on Experimental tab.
*  Version 4.2.0 - Adds logic to stop the animation and polling when the Applet is not visible. This is tested on Windows Chrome, Android Chrome and iOS Chrome.
*  Version 4.2.1 - Spelling of the %varXX% variables indicates the number of decimal places required. %varXX% = 0dp, %VarXX% = 1dp, %vArXX% = 2dp, %vaRXX% = 3dp. - No public release.
*  Version 4.3.0 - Added collapsible groups.
*  Version 4.4.0 - Added replaceable values for the State column to support other languages.  For example 'open' could be mapped to 'abierto' or open could be enhanced with HTML tags'[b]Abierto'. 
*	    		   Moved Experimental settings to new Advanceed Tab.  Added tags M2 and M3 for easy modification. Added logic for Motion and Presence sensors.
*				   Added logic for Smoke and CO detection.  Added Tags for M4 and M5.  Added logic to replace any variables using recognized state words with the equivalent value from the stateMap.
*  				   Fixed bug with duplicate entries in the state.updatedSessionList.
*  Version 4.5.0 - Remove all code relative to Pinned Rows.
*  Version 4.5.1 - Adds the UID (deviceNumber and deviceType) to create a Unique ID to allow the same device to appear on multiple rows. This is in preparation for a code update later that will expect the presence of a UID for sort order.
*  Version 4.5.2 - Adds iFrame container rows as an option.
*  Version 4.5.3 - First Public Release after hubitat bug fix - Removed from list of known issues.
*
*  Gary Milne - November 2nd, 2025 @ 7:31 PM
*
**/

/* ToDo's before release
None
*/

/* Known Issues 
Sometimes a Shade Slider will show the value Null briefly when the slider is changed until it picks up the new value.
*/

/* Ideas for future releases
Add support for Thermostats - OR - Create a standalone digital Thermostat control that can be embedded with a URL.
Add support for conditional formatting for Temperatures
Add Media Control
Add Sensors - Humidity
Reorganize the initialize() function for easier maintenance.
PIN Protect if possible.
Remove blank fields from the data payload.
Offload the Script Template to the Parent APP, FileSystem or Web Call.
*/

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.transform.Field
import java.text.SimpleDateFormat
import java.util.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.TimeZone

//These are the data for the pickers used on the child forms.
static def textScale() { return ['50', '55', '60', '65', '70', '75', '80', '85', '90', '95', '100', '105', '110', '115', '120', '125', '130', '135', '140', '145', '150', '175', '200', '250', '300', '350', '400', '450', '500'] }
static def columnWidth() { return ['50', '60', '70', '80', '90', '100', '110', '120', '130', '140', '150', '160', '170', '180', '190', '200', '210', '220', '230', '240', '250', '260', '270', '280', '290', '300', '350', '400', '450', '500'] }
static def textAlignment() { return ['Left', 'Center', 'Right', 'Justify'] }
static def opacity() { return ['1', '0.9', '0.8', '0.7', '0.6', '0.5', '0.4', '0.3', '0.2', '0.1', '0'] }
static def elementSize() { return ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20'] }
static def elementSize2() { return ['0', '0.5', '1', '1.5', '2', '2.5', '3', '3.5', '4', '4.5', '5', '5.5', '6', '6.5', '7', '7.5', '8', '8.5', '9', '9.5', '10', '11', '11.5', '12', '12.5', '13', '13.5', '14', '14.5', '15', '15.5', '16', '16.5', '17', '17.5', '18', '18.5', '19', '19.5','20'] }
static def elementSizeMinor() { return ['0', '0.1', '0.2', '0.3', '0.4', '0.5', '0.6', '0.7', '0.8', '0.9', '1'] }
static def unitsMap() { return ['°F', ' °F', '°C', ' °C']}
static def dateFormatsMap() { return [1: "To: yyyy-MM-dd HH:mm:ss.SSS", 2: "To: HH:mm", 3: "To: h:mm a", 4: "To: HH:mm:ss", 5: "To: h:mm:ss a", 6: "To: E HH:mm", 7: "To: E h:mm a", 8: "To: EEEE HH:mm", 9: "To: EEEE h:mm a", \
								10: "To: MM-dd HH:mm", 11: "To: MM-dd h:mm a", 12: "To: MMMM dd HH:mm", 13: "To: MMMM dd h:mm a", 14: "To: yyyy-MM-dd HH:mm", 15: "To: dd-MM-yyyy h:mm a", 16: "To: MM-dd-yyyy h:mm a", 17: "To: E @ h:mm a" ] }
static def dateFormatsList() { return dateFormatsMap().values() }
static def hubProperties() { return ["sunrise", "sunrise1", "sunrise2", "sunset", "sunset1", "sunset2", "hubName", "hsmStatus", "currentMode", "firmwareVersionString", "uptime", "timeZone", "daylightSavingsTime", "currentTime", "currentTime1", "currentTime2"].sort() }

static def defaultStateMap() { return '''{"open": "open", "closed": "closed", "active": "active", "inactive": "inactive", "wet": "wet", "dry": "dry", "present": "present", "not present": "not present", "detected": "detected", "clear": "clear", "tested": "tested"}''' }

static def createDeviceTypeMap() {
    def typeMap = [ 1: "Switch", 2: "Dimmer", 3: "RGB", 4: "CT", 5: "RGBW", 10: "Valve", 11:"Lock", 12: "Fan", 13: "Garage Door", 14: "Shade", 15: "Blind", 16: "Volume", 31: "Contact", 32:"Temperature", 33:"Leak", 34:"Motion", 35:"Presence", 36:"Smoke", 37:"Carbon Monoxide", 51:"Separator Row", 52:"Device Row", 53:"iFrame Row" ]
    // Create the inverse map for name-to-number lookups
    def nameToNumberMap = typeMap.collectEntries { key, value -> [value, key] }
    return [typeMap: typeMap, nameToNumberMap: nameToNumberMap]
}

static def durationFormatsMap() { return [21: "To: Elapsed Time (dd):hh:mm:ss", 22: "To: Elapsed Time (dd):hh:mm"] }
static def durationFormatsList() { return durationFormatsMap().values() }
static def invalidAttributeStrings() { return ["N/A", "n/a", " ", "-", "--", "?", "??"] }
static def devicePropertiesList() { return ["lastActive", "lastInactive", "lastActiveDuration", "lastInactiveDuration", "roomName", "colorName", "colorMode", "power", "healthStatus", "energy", "ID", "network", "deviceTypeName", "lastSeen", "lastSeenElapsed", "battery", "temperature", "colorTemperature"].sort() }
static def decimalPlaces() {return ["0 Decimal Places", "1 Decimal Place"]}
							   
@Field static final codeDescription = "<b>Remote Builder - SmartGrid 4.5.1 (8/16/25)</b>"
@Field static final codeVersion = 451
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
	//Does the initialization of variables at install, any variables added to the subsequent releases and checks for null variables created bywhen the user clicks on "No Selections".
	initialize()
	if (state.initialized == true) compile()
    		
	dynamicPage(name: "mainPage", title: "<div style='text-align:center;color: #c61010; font-size:30px;text-shadow: 0 0 5px #FFF, 0 0 10px #FFF, 0 0 15px #FFF, 0 0 20px #49ff18, 0 0 30px #49FF18, 0 0 40px #49FF18, 0 0 55px #49FF18, 0 0 75px #ffffff; margin-top:-3vh !important;'>Remote Builder - " + moduleName + " 💡 </div>", uninstall: true, install: true, singleThreaded:false) {
		
		section(hideable: true, hidden: state.hidden.Configure, title: buttonLink('btnHideConfigure', getSectionTitle("Configure"), 20)) {
                //Setup the Table Style
                paragraph "<style>#buttons1 {font-family: Arial, Helvetica, sans-serif; width:100%; text-layout:fixed; text-align:'Center'} #buttons1 td, #buttons1 tr {width: 11%; background:#00a2ed;color:#FFFFFF;text-align:Center;opacity:0.75;padding: 8px} #buttons1 td:hover {padding: 8px; background: #27ae61;opacity:1}</style>"
                table1 = "<table id='buttons1'><td>" + buttonLink ('Controls', 'Controls', 1) + "</td><td>" + buttonLink ('Sensors', 'Sensors', 2) + "</td><td>" + buttonLink ('RenameDevices', 'Rename Devices', 3) + "</td><td>" + 
					// buttonLink ('Batteries', 'Batteries', 3) + "</td><td>" + buttonLink ('Inactivity', 'Inactivity', 4) + "</td><td>" + 
					buttonLink ('Endpoints', 'Endpoints', 5) + "</td><td>" + buttonLink ('Polling', 'Polling', 6) + "</td><td>" + buttonLink ('Variables', 'Variables', 7) + "</td><td>" +
					buttonLink ('CustomRows', 'Custom Rows', 8) + "</td><td>" +	buttonLink ('Publish', 'Publish', 9) + "</td><td>" + buttonLink ('Logging', 'Logging', 10) + "</td></table>"
                paragraph table1
				
			if (state.activeButtonA == 1){ //Start of Controls Section
				// Input for selecting filter criteria
				input(name: "filter", type: "enum", title: bold("Filter Controls (optional)"), options: ["All Selectable Controls", "Power Meters", "Switches", "Color Temperature Devices", "Color Devices", "Dimmable Devices", "Valves", "Fans", "Locks", "Garage Doors", "Shades & Blinds"].sort(), required: false, defaultValue: "All Selectable Controls", submitOnChange: true, width: 2, style:"margin-right: 20px")
							
				def filterOptions = [
    				"All Selectable Controls": [capability: "capability.powerMeter, capability.switch, capability.valve, capability.lock, capability.garageDoorControl, capability.doorControl, capability.fanControl, capability.audioVolume, capability.windowShade, capability.windowBlind", title: "<b>Select Controls</b>"],
    				"Power Meters": [capability: "capability.powerMeter", title: "<b>Select Power Meter Devices</b>"], "Switches": [capability: "capability.switch", title: "<b>Select Switch Devices</b>"], "Color Temperature Devices": [capability: "capability.colorTemperature", title: "<b>Select Color Temperature Devices</b>"],
    				"Color Devices": [capability: "capability.colorControl", title: "<b>Select Color Devices</b>"], "Dimmable Devices": [capability: "capability.switchLevel", title: "<b>Select Dimmable Devices</b>"], "Valves": [capability: "capability.valve", title: "<b>Select Valves</b>"], "Fans": [capability: "capability.fanControl", title: "<b>Select Fans</b>"],
    				"Garage Doors": [capability: "capability.garageDoorControl", title: "<b>Select Garage Doors</b>"], "Locks": [capability: "capability.lock", title: "<b>Select Locks</b>"], "Shades & Blinds": [capability: "capability.windowShade, capability.windowBlind", title: "<b>Select Shades & Blinds</b>"]
				]
				// Apply switch-case logic based on the filter value
				def option = filterOptions[filter]
				if (option) { input "myDevices", option.capability, title: option.title, multiple: true, submitOnChange: true, newLine: true, width: 2, style: "margin-right:25px" } 
				else { if (isLogDebug) log.debug "No filter option selected." }
                
                paragraph line(1)
                myText =  "<b>Important: If you change the selected devices you must do a " + red("Publish and Subscribe") + " for SmartGrid to work correctly.</b><br>"
			}
			
			if (state.activeButtonA == 2){ //Start of Sensors Section
				input "myContacts", "capability.contactSensor", title: "<b>Select Contact Sensors</b>", multiple: true, submitOnChange: true, width: 2, newLine: false, style:"margin-right: 50px"
				input "myMotion", "capability.motionSensor", title: "<b>Select Motion Sensors</b>", multiple: true, submitOnChange: true, width: 2, newLine: false, style:"margin-right: 50px"
				input "myPresence", "capability.presenceSensor", title: "<b>Select Presence Sensors</b>", multiple: true, submitOnChange: true, width: 2, newLine: false, style:"margin-right: 50px"
                paragraph line(1)
                input "myLeaks", "capability.waterSensor", title: "<b>Select Water Sensors</b>", multiple: true, submitOnChange: true, width: 2, newLine: false, style:"margin-right: 50px"
                input "mySmoke", "capability.smokeDetector", title: "<b>Select Smoke Detectors</b>", multiple: true, submitOnChange: true, width: 2, newLine: false, style:"margin-right: 50px"
				input "myCarbonMonoxide", "capability.carbonMonoxideDetector", title: "<b>Select Carbon Monoxide Detectors</b>", multiple: true, submitOnChange: true, width: 2, newLine: false, style:"margin-right: 50px"
                paragraph line(1)
				input "myTemps", "capability.temperatureMeasurement", title: "<b>Select Temp Sensors</b>", multiple: true, submitOnChange: true, width: 2, newLine: false, style:"margin-right: 50px"
				input(name: "onlyReportOutsideRange", type: "enum", title: bold("Only Report Outside Range"), options: ["True", "False"], required: false, defaultValue: "False", submitOnChange: true, width: 2, style:"margin-right:50px")
				input (name: "minTemp", title: "<b>Lower Threshold</b>", type: "string", submitOnChange:true, width:2, defaultValue: "50", newLine:false, style:"margin-right: 50px")
				input (name: "maxTemp", title: "<b>Upper Threshold</b>", type: "string", submitOnChange:true, width:2, defaultValue: "90", newLine:false, style:"margin-right: 50px")
				paragraph line(1)
			}
            
            if (state.activeButtonA == 3){ //Start of Rename Devices Section - Allow users to rename devices that fit certain patterns
                if (myDeviceRenameCount == null) app.updateSetting("myDeviceRenameCount", [value: "10", type: "enum"])
                
                // Device Rename count input
                input name: "myDeviceRenameCount", title: "<b>Device Rename Count?</b>", type: "enum", options: ['0', '2', '4', '6', '8', '10'], submitOnChange: true, defaultValue: 0, style: "width:12%"
                
                // Parse the selected device rename count
                def renameCount = myDeviceRenameCount?.toInteger() ?: 0
                if (renameCount > 0) {
                    (1..renameCount).step(2).each { i ->
                        input(name: "mySearchText$i",       title: "<b>Search Device Text #$i</b>",       type: "string", submitOnChange: false, width: 2, defaultValue: "?", newLine:true)
                        input(name: "myReplaceText$i",      title: "<b>Replace Device Text #$i</b>",      type: "string", submitOnChange: false, width: 2, defaultValue: "", style:"margin-right:100px")
                        input(name: "mySearchText${i+1}",   title: "<b>Search Device Text #${i+1}</b>",   type: "string", submitOnChange: false, width: 2, defaultValue: "?")
                        input(name: "myReplaceText${i+1}",  title: "<b>Replace Device Text #${i+1}</b>",  type: "string", submitOnChange: false, width: 2, defaultValue: "", newLineAfter: true)
                    }
                    input(name: "refresh", type: "button", title: "Apply Changes", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, newLine:true)
                    paragraph line(1)
                }
			}
							      
			if (state.activeButtonA == 5){ //Start of Endpoints Section
            	input(name: "localEndpointState", type: "enum", title: bold("Local Endpoint State"), options: ["Disabled", "Enabled"], required: false, defaultValue: "Enabled", submitOnChange: true, width: 2, style:"margin-right: 20px")
                input(name: "cloudEndpointState", type: "enum", title: bold("Cloud Endpoint State"), options: ["Disabled", "Enabled"], required: false, defaultValue: "Disabled", submitOnChange: true, width: 2, style:"margin-right: 20px")
				input(name: "rebuildEndpoints", type: "button", title: "Rebuild Endpoints", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, style:"margin-top:25px; margin-left:20px")
				paragraph line (1)
				
				//Display the Endpoints with links or ask for compilation
				paragraph "<a href='${state.localEndpoint}' target=_blank><b>Local Endpoint</b></a>: ${state.localEndpoint} "
                paragraph "<a href='${state.cloudEndpoint}' target=_blank><b>Cloud Endpoint</b></a>: ${state.cloudEndpoint} "
                
				paragraph line (1)
				myText = "<b>Important: If these endpoints are not generated you may have to enable OAuth in the child application code for this application to work.</b><br>"
            	myText += "Both endpoints can be active at the same time and can be enabled or disable through this interface.<br>"
				myText += "Endpoints are paused if this instance of the <b>Remote Builder</b> application is paused. Endpoints are deleted if this instance of <b>Remote Builder</b> is removed.<br>"
				paragraph summary("Endpoint Help", myText)
                paragraph line (1)

                myText = "<b>You can use these strings to create embedded content within a web page. Just replace any [] with <>.</b><br>"
				myText += "<ul><li>[iframe name=" + state.AppID + "-A src=" + state.localEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe]</li>";
				myText += "<li>[iframe name=" + state.AppID + "-A src=" + state.cloudEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe]</li>";
				myText += "<li>[iframe name=" + state.AppID + "-B src=" + state.localEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe]</li>";
				myText += "<li>[iframe name=" + state.AppID + "-B src=" + state.cloudEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe]</li></ul>";
                paragraph summary("Embedded Links", myText)
			}
			
			if (state.activeButtonA == 6){  //Start of Polling Section
				input(name: "isPolling", type: "enum", title: bold("Endpoint Polling"), options: ["Enabled", "Disabled"], required: false, defaultValue: "Disabled", submitOnChange: true, width: 2, style:"margin-right: 20px")
				myText = "<b>A)</b> You want a more graceful refresh operation on a Hubitat Dashboard. Enable Polling and set the Event Timeout (Publishing Section) to <b>Never</b>. Doing so results in the SmartGrid updating vs doing a complete refresh.<br>"
				myText += "<b>B)</b> You want an automatic refresh operation for a SmartGrid that is being displayed directly on a device without using a Hubitat Dashboard. In this case you should also enable Polling and set the Event Timeout to <b>Never</b>. This allows you to have a SmartGrid run directly on your phone, tablet or computer and it will automatically update whenever changes are detected.<br>"
				paragraph summary(red("<b>Important: When to Enable Polling</b><br>"), myText)
				
				if (isPolling == "Enabled"){
					input (name: "pollInterval", type: "enum", title: bold('Poll Interval (seconds)'), options: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '15', '20', '30', '60'], required: false, defaultValue: '5', width: 2, submitOnChange: true, newLine:true)
					input (name: "pollUpdateColorSuccess", type: "color", title: bold2("Update Color Success", pollUpdateColorSuccess), required: false, defaultValue: "#00FF00", submitOnChange: true, width:2)
					input (name: "pollUpdateColorFail", type: "color", title: bold2("Update Color Fail", pollUpdateColorFail), required: false, defaultValue: "#FF0000", submitOnChange: true, width:2)
					input (name: "pollUpdateColorPending", type: "color", title: bold2("Update Color Pending", pollUpdateColorPending), required: false, defaultValue: "#FFA500", submitOnChange: true, width:2)
					
					input (name: "pollUpdateDuration", type: "enum", title: bold('Update Duration'), options: elementSize(), required: false, defaultValue: '2', width: 2, submitOnChange: true)
					input (name: "shuttleHeight", type: "enum", title: bold('Shuttle Height'), options: elementSize(), required: false, defaultValue: '2', width: 2, submitOnChange: true, newLine:true)
					input (name: "shuttleColor", type: "color", title: bold2("Shuttle Color", shuttleColor), required: false, defaultValue: "#000000", submitOnChange: true, width:2)
					input (name: "commandTimeout", type: "enum", title: bold('Command Timeout (seconds)'), options: ['5', '6', '7', '8', '9', '10', '12', '15', '20'], required: false, defaultValue: '10', width: 2, submitOnChange: true, newLine:true)
				}
				paragraph line(1)				
				
				myText = "You can configure the SmartGrid to poll the endpoint and apply any changes that are found. If there are no changes the SmartGrid goes back to sleep until the next poll interval.<br>"
           		myText += "<b>Poll Interval:</b> The frequency at which the Hub will be contacted to ask if there are any updates available.<br>"
				myText += "<b>Poll Update Success Color:</b> When updates are applied the Grid will be outlined in the selected color.<br>"
				myText += "<b>Poll Update Failure Color:</b> When updates are requested but no changes are received within the command timeout period the Grid will be outlined in the selected color.<br>"
				myText += "<b>Poll Update Width:</b> This setting has been deprecated. The width of the highlight color will be the same as the table outer border width.<br>"
				myText += "<b>Poll Update Duration:</b> The duration in seconds that the Success\\Failure outline is displayed.<br><br>"
				myText += "<b>Shuttle:</b> In polling mode the Shuttle is displayed at the base of the SmartGrid as a visual indicator of the polling process. When the bar hits either edge then a polling event will occur and any changes will be picked up.<br>"
				myText += "<b>Shuttle Bar Height:</b> The height of the bar at the base of the SmartGrid that identifies the position in the polling cycle.<br>"
				myText += "<b>Shuttle Bar Color:</b> The color of the bar at the base of the SmartGrid that identifies the position in the polling cycle.<br>"
				myText += "<b>Command Timeout:</b> The amount of time allowed to pass without a response from the Hub before a request is deemed to have failed.<br>"
				myText += "When the polling process discovers an update is pending then the SmartGrid is refreshed and the table is outlined for <b>X</b> seconds using the Poll Update Duration value configured above.<br>"
				myText += "<b>Note: </b> You can initiate a full refresh of the table at anytime regardless of the polling interval using the Refresh Icon <b>↻</b>."
				paragraph summary("Polling Help", myText)
				paragraph red("<b>Important: The Polling Enabled\\Disabled and Poll Interval only apply to the first time a SmartGrid is run in a browser. After that you must change these settings using the Modal window which is accessible by holding a mouse or finger down within the SmartGrid for 4 seconds.</b>")
            }
			
			if (myVariableCount == null) app.updateSetting("myVariableCount", [value: "0", type: "enum"])
            if (state.activeButtonA == 7){ //Start of Variables     
                // Variable count input
                input name: "myVariableCount", title: "<b>Source Count?</b>", type: "enum", options: (0..10), submitOnChange: true, defaultValue: 0, style: "width:12%"

                // Parse the selected variable count
                def variableCount = myVariableCount?.toInteger() ?: 0

                // Loop through each variable
                for (int i = 1; i <= variableCount; i++) {
                    def sourceSetting = settings["variableSource${i}"]
                    def sourceWidth = (sourceSetting == "Hub Variable" || sourceSetting == "Hub Property") ? "width:20.75%" : "width:10%"

                    input "variableSource${i}", "enum", title: dodgerBlue("<b>Source #$i</b>"), options: ["Device Attribute", "Hub Variable", "Hub Property", "Disabled"], submitOnChange: true, defaultValue: "Device Attribute", newLine: true, style: sourceWidth
                    def sourceType = sourceSetting

                    if (sourceType == "Device Attribute") {
                        def devKey = "myDevice${i}"
                        def dev = settings[devKey]
                        //Select a Device to use for the variable sources.
                        input devKey, "capability.*", title: "<b>Device</b>", multiple: false, required: false, submitOnChange: true, newLine: false, style: "margin-left:20px;width:10%"
                        //Allow up to 5 attributes to be selectable
                        if (dev) {
                            def attrList = getAttributeList(dev)
                            (1..5).each { j ->
                                def attrIndex = i * 10 + j
                                input "myAttribute${attrIndex}", "enum", title: "&nbsp<b>Attribute (%var${attrIndex}%)</b>", options: attrList, multiple: false, submitOnChange: true, required: false, newLine: false, style: "margin-left:20px;width:10%"
                            }
                        }
                    }

                    if (sourceType == "Hub Variable") {
                        def varList = getAllGlobalVars().findAll { it.value?.type }?.keySet()?.collect()?.sort { it.capitalize() }
                        //Allow up to 5 attributes to be selectable
                        (1..5).each { j ->
                            def attrIndex = i * 10 + j
                            input "myHubVariable${attrIndex}", "enum", title: "<b>Hub Variable (%var${attrIndex}%)</b>", submitOnChange: true, options: varList, newLine: false, style: "margin-left:20px;width:10%"
                        }
                    }

                    if (sourceType == "Hub Property") {
                        def varList = getAllGlobalVars().findAll { it.value?.type }?.keySet()?.collect()?.sort { it.capitalize() }
                        //Allow up to 5 attributes to be selectable
                        (1..5).each { j ->
                            def attrIndex = i * 10 + j
                            input "myHubProperty${attrIndex}", "enum", title: "<b>Hub Property (%var${attrIndex}%)</b>", submitOnChange: true, options: hubProperties(), newLine: false, style: "margin-left:20px;width:10%"
                        }
                    }
                }

				paragraph line(1)
				myText = "Here you can configure variables that can be placed within the SmartGrid. You can have up to 10 configured variables.<br>"
           		myText += "<b>Source:</b> This can be either a <b>Device Attribute</b> pair or a <b>Hub Variable</b>.<br>"
				myText += "<b>Variables:</b> The variables are named %var1% - %var10% as shown in the above dialogs.<br>"
				myText += "Once you have configured your variables you can place them within the SmartGrid using the Custom Rows tab using the form <b>%varX%</b>.<br>"
				paragraph summary("Variables Help", myText)
                paragraph "<b>Note:</b> A change in the value of a variable will cause the SmartGrid to refresh it's data. Choose wisely."
				paragraph red("<b>Important: Custom rows are only displayed when Custom Sort is enabled.</b>")
            }
			
			if (state.activeButtonA == 8){ //Start of Custom Rows
				input (name: "customRowCount", title: "<b>How Many Custom Rows?</b>", type: "enum", options: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20], submitOnChange:true, width:2, defaultValue: 0)				
				for (int i = 1; i <= customRowCount.toInteger(); i++) {
					input (name: "customRowType${i}", title: "<b>Row $i</b>", type: "enum", options: ["Device Row", "Separator Row", "iFrame Row", "Disabled"], submitOnChange:true, newLine: true, width:1, defaultValue: "Separator Row", style:"margin-right: 25px")
					
                    if ( settings["customRowType${i}"] != "iFrame Row") {
                    	input "myNameText${i}", "string", title: "<b>Name Column Text</b>", submitOnChange:false, width:2, defaultValue: "[b]Your Text Here (%var%)[/b]", newLine:false, style:"margin-right: 25px"    
                        input "myStateText${i}", "string", title: "<b>State Column Text</b>", submitOnChange:false, width:3, defaultValue: "", newLine:false, style:"margin-right: 25px"
						input "myControlABText${i}", "string", title: "<b>Control A/B Column Text</b>", submitOnChange:false, width:1, defaultValue: "", newLine:false, style:"margin-right: 25px"
                    	input "myControlCText${i}", "string", title: "<b>Control C Column Text</b>", submitOnChange:false, width:1, defaultValue: "", newLine:false, style:"margin-right: 25px"
						input "myInfoAText${i}", "string", title: "<b>Info 1 Text</b>", submitOnChange:false, width:1, defaultValue: "", newLine:false, style:"margin-right: 25px"
						input "myInfoBText${i}", "string", title: "<b>Info 2 Text</b>", submitOnChange:false, width:1, defaultValue: "", newLine:false, style:"margin-right: 25px"
						input "myInfoCText${i}", "string", title: "<b>Info 3 Text</b>", submitOnChange:false, width:1, defaultValue: "", newLine:false, style:"margin-right: 25px"
                    }
                    else {
                        input "myStateText${i}", "string", title: "<b>iFrame URL in form http://www.example.com</b>", submitOnChange:false, width:5, defaultValue: "[b]Your Text Here (%var%)[/b]", newLine:false, style:"margin-right: 45px"    
                    	input "myIFrameHeight${i}", "string", title: "<b>iFrame Height (px)</b>", submitOnChange:false, width:1, defaultValue: "200", newLine:false, style:"margin-right: 25px"    
                    }
                    
				}
                input(name: "refresh", type: "button", title: "Apply Changes", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2)
				paragraph red("<b>Important: Custom rows are only displayed when Custom Sort is enabled.</b>")
				
				paragraph line(1)
				myText = "Here you can configure custom lines that can be placed within the table when using a <b>Custom Sort</b>. These are generally intended as separators between functional groups within a table.<br>"
           		myText += "<b>Name Column Text X:</b> This text will be placed within the <b>Name</b> column.<br>"
				myText += "<b>State Column Text X:</b> If configured this value will be displayed within the <b>State</b> column.<br>"
				myText += "You can place static text, HTML text using [] or variables within this text. To access a variable just enter %varX% where X is the variable number defined within the <b>Variables</b> tab.<br>"
				myText += "To use a blank value for a field simply use the space bar to remove the default values."
				paragraph summary("Custom Row Help", myText)
				paragraph line(2)
								
				input(name: "isCustomSort", type: "enum", title: bold("Enable Custom Sort"), options: ['true', 'false'], required: false, defaultValue: "false", submitOnChange: true, width:1 , style:"margin-right: 50px; ")
				if (isCustomSort == "true"){
					if (isDragDrop) input(name: "EnableDragDrop", type: "button", title: "Enable Drag & Drop", backgroundColor: "orange", textColor: "white", submitOnChange: true, width: 2, style:"margin-left: 25px; margin-top: 25px;")
					else input(name: "EnableDragDrop", type: "button", title: "Enable Drag & Drop", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, style:"margin-left: 25px; margin-top: 25px;")
					input(name: "saveCustomSort", type: "button", title: " Save  Custom  Sort ", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, style:"margin-left: 25px; margin-top: 25px;")
					myText = "<b>Notes:</b><br>"
					myText += "1) If you add devices or sensors to your SmartGrid you must update your Custom Sort Order to include the new lines.<br>"
					myText += "2) " + red("<b>DO NOT EXECUTE ANY COMMANDS OR REFRESH YOUR SCREEN UNTIL YOU HAVE SAVED YOUR CUSTOM SORT OR YOUR PROGRESS WILL BE LOST.</b>")
					paragraph myText
				}
								
				myText = "To configure a Custom Sort Order follow the instructions below:<br>"
           		myText += "<b>Step 1:</b> Enable Drag & Drop.<br>"
				myText += "<b>Step 2:</b> Now Reorder the rows to your liking in the grid below using drag and drop.<br>"
				myText += "<b>Step 3:</b> Save the current sort order. (Drag and Drop will be disabled)"
				paragraph summary("Custom Sort Help", myText)
				paragraph line(2)
			}
			
			if (state.activeButtonA == 9){ //Start of Publish Section
				input(name: "myRemote", title: bold("Attribute to store the Remote?"), type: "enum", options: parent.allTileList(), required: false, submitOnChange: true, width: 3, defaultValue: 25, newLine: false)
				input(name: "myRemoteName", type: "text", title: bold("Name this Remote"), submitOnChange: true, width: 3, defaultValue: "New Remote", newLine: false, required: true)
				input(name: "tilesAlreadyInUse", type: "enum", title: bold("For Reference Only: Remotes in Use"), options: parent.getTileList(), required: false, defaultValue: "Remotes List", submitOnChange: true, width: 3)
				input(name: "eventTimeout", type: "enum", title: bold("Event Timeout (millis)"), required: false, multiple: false, defaultValue: "Never", options: ["0", "250", "500", "1000", "1500", "2000", "5000", "10000", "Never"], submitOnChange: true, width: 2)
				
				if (myRemoteName != null && myRemote != null && state.deviceList != null) {
					input(name: "publishSubscribe", type: "button", title: "Publish and Subscribe", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2)
					input(name: "unsubscribe", type: "button", title: "Delete Subscription", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2)
				} else input(name: "cannotPublish", type: "button", title: "Publish and Subscribe", backgroundColor: "#D3D3D3", textColor: "black", submitOnChange: false, width: 2)			
                
                myText = red("<b>Important: Remotes 21 - 25 each have 4 available attributes: Remote21-Local-A, Remote21-Local-B, Remote21-Cloud-A and Remote21-Cloud-B</b><br>")
                myText += "<b>Use these Remotes when you wish to run two instances of the same SmartGrid on the same browser page, such as within a Hubitat dashboard. This allows each instance to have their own unique local settings. See documnetation for more information.</b>"
                paragraph myText                 
				paragraph line(1)
				
				if (myRemoteName) app.updateLabel(myRemoteName)
				myText =  "Publishing a remote is optional and only required if it will be used within a Hubitat dashboard. Remotes can be accessed directly via the URL's in the Endpoints section and bypass the Dashboard entirely if desired.<br> "
				myText += "The <b>Event Timeout</b> period is how long Tile Builder will wait for subsequent events before publishing the table. Lowering the event timeout will make the table more responsive but also increase the number of refreshes. "
                myText += "Re-publishing a table will cause it to refresh on the dashboard unless the Event Timeout is set to Never.  In this situation you can synchronise the table using the <b>Refresh Icon</b> to synchronise the table.<br>"   
				myText += "If publishing to a dashboard is enabled then the The <b>Remote Name</b> given here will also be used as the name for this instance of Remote Builder. "
				myText += "Appending the name with your chosen remote number can make parent display more readable. Only <b>links</b> to the Local and Cloud Endpoints are stored in the Remote Builder Storage Device when publishing is enabled.<br>"
				myText += "<b>Note:</b> If you are not using the Remote within a Hubitat Dashboard you should <b>set the Event Timeout to Never</b> as republishing is not needed."
				paragraph summary("Publishing Help", myText)
        	}
			
			if (state.activeButtonA == 10){ //Start of Logging Section
				input(name: "isLogConnections", type: "bool", title: "<b>Record All Connection Requests?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogActions", type: "bool", title: "<b>Record All Action Requests?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogPublish", type: "bool", title: "<b>Enable Publishing logging?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogDeviceInfo", type: "bool", title: "<b>Enable Device Details logging?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogError", type: "bool", title: "<b>Log errors encountered?</b>", defaultValue: true, submitOnChange: true, width: 3, newLine: true)
				input(name: "isLogDebug", type: "bool", title: "<b>Enable Debug logging?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogTrace", type: "bool", title: "<b>Enable Trace logging?</b>", defaultValue: false, submitOnChange: true, width: 3)
				paragraph line(1)
				paragraph "In this section you can enable logging for various aspects of the program. This is usually used for debugging purposes and by default all logging other than errors is turned off by default. You can also rebuild the endpoints if you refresh the Oauth client secret."				
        	}
			//paragraph line (2)
		}
						
		//Start of Preview Section
		section(hideable: true, hidden: state.hidden.Preview, title: buttonLink('btnHidePreview', getSectionTitle("Preview"), 20)) {			
			input(name: "displayEndpoint", type: "enum", title: bold("Display Endpoint"), options: ["Local", "Cloud"], required: false, defaultValue: "Local", submitOnChange: true, width: 1, style:"margin-right:25px")
			input(name: "tilePreviewWidth", type: "enum", title: bold("Max Width (x200px)"), options: [1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8], required: false, defaultValue: 2, submitOnChange: true, style: "width:12%;margin-right:25px")
			input(name: "tilePreviewHeight", type: "enum", title: bold("Preview Height (x190px)"), options: [1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8], required: false, defaultValue: 2, submitOnChange: true, style: "width:12%;margin-right:25px")
			input(name: "tilePreviewBackground", type: "color", title: bold("Preview Background Color"), required: false, defaultValue: "#000000", width: 2, submitOnChange: true, style: "margin-right:25px")
			if (myRemoteName != null && myRemote != null && state.deviceList != null) input(name: "publishSubscribe", type: "button", title: "Publish and Subscribe", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, style:"margin-top:20px;margin-right:25px")
			else input(name: "cannotPublish", type: "button", title: "Publish and Subscribe", backgroundColor: "#D3D3D3", textColor: "white", submitOnChange: false, width: 2, style:"margin-top:20px;margin-right:25px")

			myMaxWidth = ( (tilePreviewWidth.toFloat() * 210) - 10 ) + 3 * 2	//Makes the width and height 6px larger account for the border around the iframe so the resulting iframe is exactly the same size as the dashboard tile.
			myMaxHeight = ( (tilePreviewHeight.toFloat() * 200) - 10 ) + 3 * 2  
			//log.info ("myMaxWidth is: $myMaxWidth and myMaxHeight is: $myMaxHeight (includes 6xp for borders.")
			
			if (displayEndpoint == "Local") paragraph """<div style="margin-left: 25px; background-color: ${tilePreviewBackground}; padding: 10px; border: 2px solid black; border-radius: 10px;">
				<iframe name="${state.AppID}-P" src="${state.localEndpoint}" width="${myMaxWidth.toInteger()}" height="${myMaxHeight.toInteger()}" style="padding: 0px; background-color: ${tilePreviewBackground}; border: 3px dashed white; border-radius: 10px;" scrolling="no"></iframe>
				</div>"""
			//log.info ("EP $display
			if (displayEndpoint == "Cloud") paragraph """<div style="margin: 25px; background-color: ${tilePreviewBackground}; padding: 10px; border: 2px solid black; border-radius: 10px;">
				<iframe name="${state.AppID}-P" src="${state.cloudEndpoint}" width="${myMaxWidth.toInteger()}" height="${myMaxHeight.toInteger()}" style="padding: 0px; background-color: ${tilePreviewBackground}; border: 3px dashed white; border-radius: 10px;" scrolling="no"></iframe>
				</div>"""

			myText = "The preview window above is optimized for the default Hubitat Dashboard tile size of 200px wide by 190px tall. Tiles greater than 1x1 will be slightly larger than direct multiples of this number because space previously allocated between tiles is now part of the tile.<br>"
			myText += "If you wish to maximize your tile space by shrinking the gap between tiles you can change the <b>Grid Gap</b> using the Dashboard Grid menu. Or you could use the following CSS: <br>"
			myText+=  "<mark>[class*='tile-title']{height:0% !important; visibility:hidden;}</mark>    <mark>[class*='tile-contents']{width:100% !important; height:100% !important; padding:0px;}</mark><br>"
			myText += "To help visualize the dashboard tile edges and add an orange dashed outline to each tile you could use CSS like this:<br><mark>[class*='tile-primary']{outline: 1px dashed orange;}</mark>"
			paragraph summary("Preview Notes", myText)
			//paragraph line(1)
		}

		//Start of Design Section
		section(hideable: true, hidden: state.hidden.Design, title: buttonLink('btnHideDesign', getSectionTitle("Design"), 20)) {						
			//Setup the Table Style
			paragraph "<style>#buttons2 {font-family: Arial, Helvetica, sans-serif; width:100%; text-layout:fixed; text-align:'Center'} #buttons2 td, #buttons2 tr {width: 10%; background:#00a2ed;color:#FFFFFF;text-align:Center;opacity:0.75;padding: 4px} #buttons2 td:hover {padding: 4px; background: #27ae61;opacity:1}</style>"
			table2 = "<table id='buttons2'><td>"  + buttonLink ('General', 'General', 21) + "</td><td>" + buttonLink ('Appearance', 'Appearance', 22) + "</td><td>" + buttonLink ('Title', 'Title', 23) + "</td><td>" + 
					buttonLink ('Columns', 'Columns', 24) + "</td><td>" + buttonLink ('Padding', 'Padding', 25) + "</td><td>" + buttonLink ('Advanced', 'Advanced', 26) + "</td><td>" + buttonLink ('Experimental', 'Experimental', 27) + "</td></table>"
			paragraph table2

			if (state.activeButtonB == 21){ //General
				input(name: "defaultDateTimeFormat", title: bold("Date Time Format"), type: "enum", options: dateFormatsMap(), submitOnChange: true, defaultValue: 3, width: 2, style:"margin-right:25px")
				input(name: "defaultDurationFormat", title: bold("Duration Format"), type: "enum", options: durationFormatsMap(), submitOnChange: true, defaultValue: 21, width: 2, style:"margin-right:25px")
				input(name: "controlSize", title: bold("Control Size"), type: "enum", options: ["7.5", "10", "12.5", "15", "17.5", "20", "22.5", "25", "27.5", "30"], submitOnChange: true, defaultValue: "15", width: 2, style:"margin-right:25px")
				input (name: "ha", type: "enum", title: bold("Horizontal Alignment"), required: false, options: ["Stretch", "Left", "Center", "Right" ], defaultValue: "Stretch", submitOnChange: true, width: 2, style:"margin-right:25px", newLine: true)
				input(name: "invalidAttribute", title: bold("Invalid Attribute String"), type: "enum", options: invalidAttributeStrings(), submitOnChange: true, defaultValue: "N/A", width: 2, style:"margin-right:25px", newLine:true)
				input ("tempUnits", "enum", title: "<b>Temperature Units</b>", options: unitsMap(), multiple: false, submitOnChange: true, width: 2, required: false, defaultValue: "°F", style:"margin-right:25px")
				input ("tempDecimalPlaces", "enum", title: "<b>Temperature Decimal Places</b>", options: ["0 Decimal Places", "1 Decimal Place"], multiple: false, defaultValue: "0 Decimal Places", submitOnChange: true, width: 2, required: false, style:"margin-right:25px",)
                input ("capitalizeStrings", "enum", title: "<b>Capitalize Strings</b>", options: ["True", "False"], multiple: false, defaultValue: "False", submitOnChange: true, width: 2, required: false)
				input(name: "sortHeaderHintAZ", type: "color", title: bold("Sort Header Hint A-Z"), required: false, defaultValue: "#00FF00", submitOnChange: true, width: 2, style:"margin-right:25px", newLine: true )
				input(name: "sortHeaderHintZA", type: "color", title: bold("Sort Header Hint Z-A"), required: false, defaultValue: "#FF0000", submitOnChange: true, width: 2, style:"margin-right:25px" )
			}
			if (state.activeButtonB == 22){ //Appearance
				input(name: "hts", type: "enum", title: bold("Header Text Size %"), options: textScale(), required: false, defaultValue: "125", width: 2, submitOnChange: true, style:"margin-right:25px; margin-left:20px")
				input(name: "htc", type: "color", title: bold2("Header Text Color", htc), required: false, defaultValue: "#000000", width: 2, submitOnChange: true)
				input(name: "hbc", type: "color", title: bold2("Header Background Color", hbc), required: false, defaultValue: "#2375b8", width: 2, submitOnChange: true)
				input(name: "hbo", type: "enum", title: bold("Header Opacity"), options: opacity(), required: false, defaultValue: "1", width:2, submitOnChange: true)
				paragraph line(2)
				input (name: "crts", type: "enum", title: bold("Custom Row Text Size %"), options: textScale(), required: false, defaultValue: "100", submitOnChange: true, width: 2, style:"margin-right:25px; margin-left:20px")
				input (name: "crtc", type: "color", title: bold2("Custom Row Text Color", crtc), required: false, defaultValue: "#000000" , submitOnChange: true, width: 2)
				input (name: "crbc", type: "color", title: bold2("Custom Row Background Color #1", crbc), required: false, defaultValue: "#b6d7f1" , submitOnChange: true, width: 2)
				input (name: "crbc2", type: "color", title: bold2("Custom Row Background Color #2", crbc2), required: false, defaultValue: "#2375b8" , submitOnChange: true, width: 2)
				paragraph line(2)
				input(name: "rts", type: "enum", title: bold("Device Row Text Size %"), options: textScale(), required: false, defaultValue: "100", submitOnChange: true, width:2, style:"margin-right:25px; margin-left:20px")
				input(name: "rtc", type: "color", title: bold2("Device Row Text Color", rtc), required: false, defaultValue: "#000000", submitOnChange: true, width:2)
				input(name: "rbc", type: "color", title: bold2("Device Row Background Color", rbc), required: false, defaultValue: "#b6d7f1", submitOnChange: true, width:2)
				input(name: "rbo", type: "enum", title: bold("Device Row Background Opacity"), options: opacity(), required: false, defaultValue: "1", submitOnChange: true, width:2)	
				paragraph line(2)	
				input(name: "highlightSelectedRows", type: "enum", title: bold("Highlight Selected Rows"), options: ["True", "False"], required: false, defaultValue: "True", submitOnChange: true, width: 2, newLine: true, style:"margin-right:25px; margin-left:20px")
				if (highlightSelectedRows == "True" ) input(name: "rbs", type: "color", title: bold2("Selected Row Background Color", rbs), required: false, defaultValue: "#fdf09b", submitOnChange: true, width:2)
				paragraph line(2)

				input (name: "bc", type: "color", title: bold2("Border Color", bc), required: false, defaultValue: "#000000", submitOnChange: true, width: 2, style:"margin-right:25px; margin-left: 20px;" )
				input (name: "bwo", type: "enum", title: bold('Border Width - Outer'), options: elementSize2(), required: false, defaultValue: '2.5', width: 2, submitOnChange: true, style:"margin-right:25px;" )
				input (name: "bwi", type: "enum", title: bold('Border Width - Inner'), options: elementSize2(), required: false, defaultValue: '2', width: 2, submitOnChange: true)
				paragraph line(2)

				input (name: "theme", type: "enum", title: bold('Select Color Theme'), options: ['Blue','Green','Orange', 'Brown', 'Purple', 'Pink', 'Mono'], required: false, defaultValue: 'Blue', width: 2, submitOnChange: true)
				input (name: "applyTheme", type: "button", title: "Apply Theme", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, style:"margin-left:20px;margin-top:25px;")
			}

			if (state.activeButtonB == 23){ //Title
				input(name: "tt", type: "string", title: bold("Title Text (? to disable)"), required: false, defaultValue: "[b]My Title[/b]?", submitOnChange: true, width: 2, style:"margin-right:25px" )
				input(name: "ts", type: "enum", title: bold("Text Size %"), options: textScale(), required: false, defaultValue: "150", submitOnChange: true, width: 2, style:"margin-right:25px" )
				input(name: "tp", type: "enum", title: bold("Text Padding"), options: elementSize(), required: false, defaultValue: "5", submitOnChange: true, width: 2, style:"margin-right:25px" )
				input(name: "ta", type: "enum", title: bold("Text Alignment"), options: textAlignment(), required: false, defaultValue: "Center", submitOnChange: true, width: 2, style:"margin-right:25px" )
				input(name: "tc", type: "color", title: bold2("Text Color", tc), required: false, defaultValue: "#000000", submitOnChange: true, width: 2, newLine:true, style:"margin-right:25px" )
				input(name: "tb", type: "color", title: bold2("Background Color", tb), required: false, defaultValue: "#a09fce", submitOnChange: true, width: 2, style:"margin-right:25px" )
				input(name: "to", type: "enum", title: bold("Background Opacity"), options: opacity(), required: false, defaultValue: "1", submitOnChange: true, width:2, style:"margin-right:25px" )
			}

			if (state.activeButtonB == 24){  //Columns
				input(name: "hideColumn1", type: "bool", title: bold("Hide Column 1 - Selection Boxes?"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px; margin-left:10px", newLine:true)
				input(name: "hideColumn2", type: "bool", title: bold("Hide Column 2 - Icons?"), required: false, defaultValue: false, width:2, submitOnChange: true, style:"margin-top:40px;", newLine:false)			
                input(name: "column3Header", type: "string", title: bold("Column 3 Header"), required: false, defaultValue: "Name", width: 2, submitOnChange: true, style:"margin-top:20px;", newLine:false)
				input(name: "hideColumn4", type: "bool", title: bold("Hide Column 4 - State?"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px;", newLine:true)
                
				input(name: "hideColumn5", type: "bool", title: bold("Hide Column 5 - Control A/B - Level/°K?"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px", newLine:true)
				if (hideColumn5 == false) {
					input(name: "column5Header", type: "string", title: bold("Column 5 Header"), required: false, defaultValue: "Control A/B", width: 2, submitOnChange: true)
					input(name: "column5Width", type: "enum", title: bold("Column 5 Width (px)"), options: columnWidth(), required: false, defaultValue: 100, submitOnChange: true, width: 2, style:"margin-right:25px") 
				}

				input(name: "hideColumn6", type: "bool", title: bold("Hide Column 6 - Control C - Color?"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px", newLine:true)
				if (hideColumn6 == false) {
					input(name: "column6Header", type: "string", title: bold("Column 6 Header"), required: false, defaultValue: "Control C", width: 2, submitOnChange: true)
					input(name: "column6Width", type: "enum", title: bold("Column 6 Width (px)"), options: columnWidth(), required: false, defaultValue: 100, submitOnChange: true, width: 2, style:"margin-right:25px")
				}

				input(name: "hideColumn7", type: "bool", title: bold("Hide Column 7 - Info 1"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px", newLine:true)
				if (hideColumn7 == false) {
					input(name: "column7Header", type: "string", title: bold("Info 1 Header Text"), required: false, width: 2, submitOnChange: true)	
					input(name: "its1", type: "enum", title: bold("Size %"), options: textScale(), required: false, defaultValue: "80", width: 2, submitOnChange: true)
					input(name: "ita1", type: "enum", title: bold("Alignment"), options: textAlignment(), required: false, defaultValue: "Center", width: 2, submitOnChange: true)
					input(name: "info1Source", type: "enum", title: bold("Data Source"), required: false, multiple: false, defaultValue: "lastActive", options: devicePropertiesList(), submitOnChange: true, width: 2)
				}
				input(name: "hideColumn8", type: "bool", title: bold("Hide Column 8 - Info 2"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px", newLine:true)
				if (hideColumn8 == false) {
					input(name: "column8Header", type: "string", title: bold("Info 2 Header Text"), required: false, width: 2, submitOnChange: true)
					input(name: "its2", type: "enum", title: bold("Size %"), options: textScale(), required: false, defaultValue: "80", width: 2, submitOnChange: true)
					input(name: "ita2", type: "enum", title: bold("Alignment"), options: textAlignment(), required: false, defaultValue: "Center", width: 2, submitOnChange: true)
					input(name: "info2Source", type: "enum", title: bold("Data Source"), required: false, multiple: false, defaultValue: "lastActiveDuration", options: devicePropertiesList(), submitOnChange: true, width: 2)
				}
				input(name: "hideColumn9", type: "bool", title: bold("Hide Column 9 - Info 3"), required: false, defaultValue: false, width:3, submitOnChange: true, style:"margin-top:40px", newLine:true)
				if (hideColumn9 == false) {
					input(name: "column9Header", type: "string", title: bold("Info 3 Header Text"), required: false, width: 2, submitOnChange: true)
					input(name: "its3", type: "enum", title: bold("Size %"), options: textScale(), required: false, defaultValue: "80", width: 2, submitOnChange: true)
					input(name: "ita3", type: "enum", title: bold("Alignment"), options: textAlignment(), required: false, defaultValue: "Center", width: 2, submitOnChange: true)
					input(name: "info3Source", type: "enum", title: bold("Data Source"), required: false, multiple: false, defaultValue: "roomName", options: devicePropertiesList(), submitOnChange: true, width: 2)
				}	
				input(name: "hideColumn11", type: "bool", title: bold("Hide Column 11 - Custom Sort (Diagnostic)"), required: false, defaultValue: true, width:2, submitOnChange: true, style:"margin-top:40px", newLine:true)
                input(name: "hideColumn12", type: "bool", title: bold("Hide Column 12 - Group (Diagnostic)"), required: false, defaultValue: true, width:2, submitOnChange: true, style:"margin-top:40px", newLine:true)
			}
            
			if (state.activeButtonB == 25){ //Padding
				input (name: "tpad", type: "enum", title: bold('Table Edge Padding'), options: elementSize(), required: false, defaultValue: '1', width: 2, submitOnChange: true, style:"margin-right:25px" )
				input (name: "tmt", type: "enum", title: bold("Top Margin"), options: elementSize(), required: false, defaultValue: "0", submitOnChange: true, width: 2, style:"margin-right:25px" )
				paragraph line(1)
				input (name: "thp", type: "enum", title: bold("Column Horizontal Padding"), options: elementSize(), required: false, defaultValue: 5, submitOnChange: true, width: 2, style:"margin-right:25px" )
				input (name: "tvp", type: "enum", title: bold("Row Vertical Padding - Major"), options: elementSize(), required: false, defaultValue: 3, submitOnChange: true, width: 2, style:"margin-right:25px" )
				input (name: "tvpm", type: "enum", title: bold("Row Vertical Padding - Minor"), options: elementSizeMinor(), required: false, defaultValue: "0", submitOnChange: true, width: 2, style:"margin-right:25px" )
			}

			if (state.activeButtonB == 26){ //Advanced
                if (markTag == null ) app.updateSetting("markTag", value: "background-color:yellow; color:black; padding:0.05em 0.25em; border-radius:0.3em; outline:1px dotted #000000; font-weight:bold;", type:"text")
                if (m1Tag == null) app.updateSetting("m1Tag", value: "background-color:dodgerBlue; color:white; padding:0.1em 0.4em;border-radius: 0.4em;outline: 1px dashed black; font-weight:bold;", type:"text")
                if (m2Tag == null) app.updateSetting("m2Tag", value: "background-color:lime; color:black; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", type:"text")
                if (m3Tag == null) app.updateSetting("m3Tag", value: "background-color:indianRed; color:white; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", type:"text")
                if (m4Tag == null) app.updateSetting("m4Tag", value: "background-color:orange; color:white; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", type:"text")
                if (m5Tag == null) app.updateSetting("m5Tag", value: "background-color:gray; color:white; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", type:"text")
                
                def part1 ="<b>Here you can configure some CSS style tags that you can reference in your SmartGrid to draw attention to a value.</b> Use something like: <b><mark> [m1]%var32%[/m1] </mark></b><br>"
                def part2 = "You often do not need to add the closing tags but if you have any formatting issues you can add them. To restore the default value for a tag just delete the contents of the field.<br>"
                paragraph part1 + part2
                            
                input (name: "markTag", type: "text", title: bold("Enter or Edit a String for the CSS formatting of the <mark> [mark] </mark> HTML tag."), required: false, newLine:false, defaultValue: "background-color:yellow; color:black; padding:0.05em 0.25em; border-radius:0.3em; outline:1px dotted #000000; font-weight:bold;", width:6, submitOnChange:true, style: "border-radius: 0.3em; outline: 2px solid #000000")
                input (name: "m1Tag", type: "text", title: bold("Enter or Edit a String for the CSS formatting of the <mark> [m1] </mark> HTML tag."), required: false, newLine: false, defaultValue: "background-color:dodgerBlue; color:white; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black;font-weight:bold;", width:6, submitOnChange:true, style: "border-radius: 0.3em; outline: 2px solid #000000")
                input (name: "m2Tag", type: "text", title: bold("Enter or Edit a String for the CSS formatting of the <mark> [m2] </mark> HTML tag."), required: false, newLine: false, defaultValue: "background-color:lime; color:black; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black;font-weight:bold;", width:6, submitOnChange:true, style: "border-radius: 0.3em; outline: 2px solid #000000")
                input (name: "m3Tag", type: "text", title: bold("Enter or Edit a String for the CSS formatting of the <mark> [m3] </mark> HTML tag."), required: false, newLine: false, defaultValue: "background-color:indianRed; color:white; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", width:6, submitOnChange:true, style: "border-radius: 0.3em; outline: 2px solid #000000")
                input (name: "m4Tag", type: "text", title: bold("Enter or Edit a String for the CSS formatting of the <mark> [m4] </mark> HTML tag."), required: false, newLine: false, defaultValue: "background-color:orange; color:white; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", width:6, submitOnChange:true, style: "border-radius: 0.3em; outline: 2px solid #000000")
                input (name: "m5Tag", type: "text", title: bold("Enter or Edit a String for the CSS formatting of the <mark> [m5] </mark> HTML tag."), required: false, newLine: false, defaultValue: "background-color:gray; color:white; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", width:6, submitOnChange:true, style: "border-radius: 0.3em; outline: 2px solid #000000")
                               
                paragraph line(2)
                part1 = "<b>Here you can modify the default values for <b>State</b> keywords that do not display in icon form. </b><br>"
                part2 = "Examples of these are <mark> open, closed, active, inactive, wet, dry, present, not present </mark> etc. For example 'open' could be changed to 'Abierto'"
                paragraph part1 + part2
                input (name: "deviceStateMap", type: "text", title: "<b>Modify the values in the map below to change the values displayed in the State column. Clear string to restore the default values. Use tags in the form [m1]Active[/m1]</b>", required: false, defaultValue: '''{"open": "open", "closed": "closed", "active": "active", "inactive": "inactive", "wet": "wet", "dry": "dry", "present": "present", "not present": "not present", "detected": "detected", "clear": "clear", "tested": "tested"}''', width:12, submitOnChange:true, style: "border-radius: 0.3em; outline: 2px solid #000000")
			}
            
            if (state.activeButtonB == 27){ //Experimental
                paragraph "<b>You will find experimental settings here if there are any.<b>"
			}

			paragraph line(1)
			paragraph "<b>Important: You must do a " + red("Publish and Subscribe") + " for SmartGrid to receive updates and work correctly in polling mode or to update automatically in the above window!</b><br>"
		}
        
		//Start of Footer Section
        section {
            //Now add a footer.
            myDocURL = "<a href='https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote_Builder_SmartGrid_Help.pdf' target=_blank> <i><b>Remote Builder - SmartGrid Help</b></i></a>"
			
			myText = '<div style="display: flex; justify-content: space-between;">'
            myText += '<div style="text-align:left;font-weight:small;font-size:12px"> <b>Documentation:</b> ' + myDocURL + '</div>'
            myText += '<div style="text-align:center;font-weight:small;font-size:12px">Version: ' + codeDescription + '</div>'
            myText += '<div style="text-align:right;font-weight:small;font-size:12px">Copyright 2022 - 2025</div>'
            myText += '</div>'
            paragraph myText
        }
        //End of Footer Section
    }
}


//*******************************************************************************************************************************************************************************************
//**************
//**************  Standard System Elements
//**************
//*******************************************************************************************************************************************************************************************

//Configures all of the default settings values. This allows us to have some parts of the settings not be visible but still have their values initialized.
//We do this to avoid errors that might occur if a particular setting were referenced but had not been initialized.
def initialize() {
	
	//Handle the initial configurations of variables
    if (state.initialized != true) {
        if (isLogTrace) log.trace("<b>initialize: Initializing all variables.</b>")
		//This is a first time install so the variables should all be current.
		//Device Selection
		app.updateSetting("filter", [value: "All Selectable Controls", type: "enum"])

		//Endpoints and Polling
		createAccessToken()
		state.localEndpoint = "${getFullLocalApiServerUrl()}/tb?access_token=${state.accessToken}"
		state.cloudEndpoint = "${getFullApiServerUrl()}/tb?access_token=${state.accessToken}"
		state.localEndpointData = "${getFullLocalApiServerUrl()}/tb/data?access_token=${state.accessToken}"
		state.cloudEndpointData = "${getFullApiServerUrl()}/tb/data?access_token=${state.accessToken}"
		state.localEndpointPoll = "${getFullLocalApiServerUrl()}/tb/poll?access_token=${state.accessToken}"
		state.cloudEndpointPoll = "${getFullApiServerUrl()}/tb/poll?access_token=${state.accessToken}"

		app.updateSetting("isPolling", [value: "Enabled", type: "enum"])
		app.updateSetting("pollInterval", "3")
		app.updateSetting("pollUpdateColorSuccess", [value: "#00FF00", type: "color"])
		app.updateSetting("pollUpdateColorFail", [value: "#FF0000", type: "color"])
		app.updateSetting("pollUpdateColorPending", [value: "#FFA500", type: "color"])
		app.updateSetting("pollUpdateDuration", [value: "2", type: "enum"])
		app.updateSetting("shuttleColor", [value: "#99C5FF", type: "color"])
		app.updateSetting("shuttleHeight", [value: "3", type: "enum"])

		//Tile Size
		app.updateSetting("tilePreviewWidth", "3")
		app.updateSetting("tilePreviewHeight", "2")
		app.updateSetting("tilePreviewBackground", [value: "#696969", type: "color"])

		//Global Settings
		app.updateSetting("invalidAttribute", [value: "N/A", type: "enum"])
		app.updateSetting("defaultDateTimeFormat", 3)
		app.updateSetting("defaultDurationFormat", 21)

		//Table Properties
		app.updateSetting("controlSize", [value: "15", type: "enum"])
		app.updateSetting("thp", "5")
		app.updateSetting("tvp", "3")
		app.updateSetting("tvpm", "0")
		app.updateSetting("tmt", "0")
		app.updateSetting("ha", [value: "Stretch", type: "enum"])
		app.updateSetting("va", [value: "Center", type: "enum"])

		//General Properties
		app.updateSetting("tempUnits", [value: "°F", type: "enum"])
		app.updateSetting("sortHeaderHintAZ", [value: "#00FF00", type: "color"])
		app.updateSetting("sortHeaderHintZA", [value: "#FF0000", type: "color"])
		app.updateSetting("tempDecimalPlaces", [value: "0 Decimal Places", type: "enum"])
		
		//Column Properties
		app.updateSetting("column2Header", "Icon")
		app.updateSetting("column3Header", "Name")
		app.updateSetting("column4Header", "State")
		app.updateSetting("column5Header", "Control A/B")
		app.updateSetting("column6Header", "Control C")
		app.updateSetting("column7Header", "Last Active")
		app.updateSetting("column8Header", "Duration")
		app.updateSetting("column9Header", "Room")
		app.updateSetting("column11Header", "Custom Sort")
		
		//Hidden Columns
		app.updateSetting("hideColumn1", false)
		app.updateSetting("hideColumn2", false)
		app.updateSetting("hideColumn3", false)
		app.updateSetting("hideColumn4", false)
		app.updateSetting("hideColumn5", false)
		app.updateSetting("hideColumn6", false)
		app.updateSetting("hideColumn7", true)
		app.updateSetting("hideColumn8", true)
		app.updateSetting("hideColumn9", true)
		app.updateSetting("hideColumn10", true)
		app.updateSetting("hideColumn11", true)
        app.updateSetting("hideColumn12", true)

		//Info Column Properties
		app.updateSetting("info1Source", "lastActive")
		app.updateSetting("its1", "80")
		app.updateSetting("ita1", "Center")
		app.updateSetting("info2Source", "lastActiveDuration")
		app.updateSetting("its2", "80")
		app.updateSetting("ita2", "Center")
		app.updateSetting("info3Source", "roomName")
		app.updateSetting("its3", "80")
		app.updateSetting("ita3", "Center")

		 //Title Properties
		app.updateSetting("tt", "?")
		app.updateSetting("ts", "125")
		app.updateSetting("tp", "5")
		app.updateSetting("tc", [value: "#000000", type: "color"])
		app.updateSetting("tb", [value: "#a09fce", type: "color"])
		app.updateSetting("ta", "Center")
		app.updateSetting("to", "1")

		//Header Properties
		app.updateSetting("hts", "100")
		app.updateSetting("htc", [value: "#ffffff", type: "color"])
		app.updateSetting("hbc", [value: "#2375b8", type: "color"])
		app.updateSetting("hbo", "1")

		//Row Properties
		app.updateSetting("rts", "90")
		app.updateSetting("rtc", [value: "#000000", type: "color"])
		app.updateSetting("rbc", [value: "#cccccc", type: "color"])
		app.updateSetting("rbo", "1")

		app.updateSetting("highlightSelectedRows", "True")
		app.updateSetting("rbs", [value: "#FFE18F", type: "color"])

		//Borders
		app.updateSetting("bc", [value: "#000000", type: "color"])
		app.updateSetting("bwo", [value: "4", type: "enum"] )  //Border Width - Outer
		app.updateSetting("bwi", [value: "2", type: "enum"] )  //Border Width - Inner
		app.updateSetting("tpad", [value: "3", type: "enum"] )  //Border Width - Inner

		//Publishing
		app.updateSetting("mySelectedRemote", "")
		app.updateSetting("publishEndpoints", [value: "Local", type: "enum"])
		app.updateSetting("eventTimeout", "Never")

		//Set initial Log settings
		app.updateSetting('isLogConnections', false)
		app.updateSetting('isLogActions', true)
		app.updateSetting('isLogPublish', false)
		app.updateSetting('isLogDeviceInfo', false)
		app.updateSetting('isLogError', true)
		app.updateSetting('isLogDebug', false)
		app.updateSetting('isLogTrace', false)

		//Have all the sections expanded to begin with except devices
		state.updatedSessionList = []
		state.compiledLocal = "<span style='font-size:32px;color:yellow'>No Devices or Not Published!</span><br>"
		state.compiledCloud = "<span style='font-size:32px;color:yellow'>No Devices or Not Published!</span><br>"
		
		//Set the initial Color Theme
		applyTheme()

		//Set the initialization flag so this whole section only happens once.
		state.initialized = true
				
    } //End of first time initialization of variables.
        
	//Start of section where variables are added to the app after the initial release and may need to be initialised
	if (state.hidden == null) state.hidden = [Configure: false, Preview: false, Design: false]	
	if (state.activeButtonA == null ) state.activeButtonA = 1
	if (state.activeButtonB == null ) state.activeButtonB = 21
	if (customRowCount == null)	app.updateSetting("customRowCount", [value: "0", type: "enum"])
	if (isCustomSort == null) app.updateSetting("isCustomSort", false)
	if (state.customSortOrder == null) state.customSortOrder = JsonOutput.toJson([ [ID: "1", row: 1] ])
	if (isDragDrop == null) app.updateSetting("isDragDrop", false)
    
    if (defaultDateTimeFormat == null ) app.updateSetting("defaultDateTimeFormat", [value: "0", type: "enum"])
    if (defaultDurationFormat == null ) app.updateSetting("defaultDurationFormat", [value: "0", type: "enum"])  
    if (controlSize == null ) app.updateSetting("controlSize", [value: "15", type: "enum"])  
	
	if (localEndpointState == null ) app.updateSetting("localEndpointState", [value: "Enabled", type: "enum"])
	if (cloudEndpointState == null ) app.updateSetting("cloudEndpointState", [value: "Disabled", type: "enum"])
	
	//Custom Row Properties
	if (crbc == null ) app.updateSetting("crbc", [value: "#b6d7f1", type: "color"])
	if (crbc2 == null ) app.updateSetting("crbc2", [value: "#2375b8", type: "color"])
	if (crtc == null ) app.updateSetting("crtc", [value: "#000000", type: "color"])
	if (crts == null ) app.updateSetting("crts", [value: "100", type: "enum"] )
	
	if (state.hidden.Preview == null ) state.hidden.Preview ? false : true
					
	//Start of Section where we check for the presence of null values, usually caused by the user selecting "No Selection" in a dialog box.
	if (tilePreviewWidth == null) app.updateSetting("tilePreviewWidth", [value: "3", type: "enum"])	
	if (tilePreviewHeight == null) app.updateSetting("tilePreviewHeight", [value: "2", type: "enum"])	
	if (myRemote == null) app.updateSetting("myRemote", [value: "20", type: "enum"])
	if (myRemoteName == null) app.updateSetting("myRemoteName", "New Remote")	
	if (commandTimeout == null ) app.updateSetting("commandTimeout", [value: "10", type: "enum"])
	if (displayEndpoint == null) app.updateSetting("displayEndpoint", [value: "Local", type: "enum"])
	
	if (pollInterval == null ) app.updateSetting("pollInterval", [value: "3", type: "enum"])
	if (pollUpdateWidth == null ) app.updateSetting("pollUpdateWidth", [value: "3", type: "enum"])
	if (pollUpdateDuration == null ) app.updateSetting("pollUpdateDuration", [value: "2", type: "enum"])
	if (shuttleHeight == null ) app.updateSetting("shuttleHeight", [value: "3", type: "enum"])
	
	if (tvp == null ) app.updateSetting("tvp", [value: "3", type: "enum"])
    if (tvpm == null ) app.updateSetting("tvpm", [value: "0", type: "enum"])
    if (tmt == null ) app.updateSetting("tmt", [value: "0", type: "enum"])
	if (thp == null ) app.updateSetting("thp", [value: "5", type: "enum"])
    if (tpad == null ) app.updateSetting("tpad", [value: "5", type: "enum"])
	if (hts == null ) app.updateSetting("hts", [value: "100", type: "enum"])
	if (hbo == null ) app.updateSetting("hbo", [value: "1", type: "enum"])
	if (rts == null ) app.updateSetting("rts", [value: "90", type: "enum"])
	if (rbo == null ) app.updateSetting("rbo", [value: "1", type: "enum"])
	if (its1 == null ) app.updateSetting("its1", [value: "80", type: "enum"])
	if (its2 == null ) app.updateSetting("its2", [value: "80", type: "enum"])
	if (its3 == null ) app.updateSetting("its3", [value: "80", type: "enum"])
	if (ita1 == null ) app.updateSetting("ita1", [value: "Center", type: "enum"])
	if (ita2 == null ) app.updateSetting("ita2", [value: "Center", type: "enum"])
	if (ita3 == null ) app.updateSetting("ita3", [value: "Center", type: "enum"])
    
    if (temperatureDecimalPlaces == null ) app.updateSetting("temperatureDecimalPlaces", [value: "0 Decimal Places", type: "enum"])
    if (capitalizeStrings == null ) app.updateSetting("capitalStrings", [value: "False", type: "enum"])
	if (myDeviceRenameCount == null ) app.updateSetting("myDeviceRenameCount", [value: "0", type: "enum"])
    if (tempUnits == null) app.updateSetting("tempUnits", [value: "°F", type: "enum"])
	
    if (hideColumn11 == null) app.updateSetting("hideColumn11", true)
    if (hideColumn12 == null) app.updateSetting("hideColumn12", true)
    
    if ( deviceStateMap == null) { app.updateSetting("deviceStateMap", [value: defaultStateMap(), type: "text"]) }
    
    //These are settings that we want to force once
	if ( state.variablesVersion < codeVersion){
		state.variablesVersion = codeVersion	
	}
    
    //Here we fix a spelling Mistake. Change Seperator to Separator
    (1..customRowCount.toInteger()).each { i ->
    	def rowType = settings["customRowType$i"]?.toString()
    	if (rowType == "Seperator Row") {
        	log.info ("Updating setting")
        	app.updateSetting("customRowType$i", [value: "Separator Row", type: "enum"])
        	rowType = "Separator Row"
        }
    }
}

//Sets the basic table colors.  //Blue is the default.
def applyTheme() {
    def themes = [
        Blue: [ crbc: "#b6d7f1", crbc2: "#2375b8", crtc: "#000000", htc: "#ffffff", hbc: "#2375b8", rtc: "#000000", rbc: "#cccccc" ],
        Green: [ crbc: "#c8e6c9", crbc2: "#388e3c", crtc: "#000000", htc: "#ffffff", hbc: "#388e3c", rtc: "#000000", rbc: "#d6e8d4" ],
		Orange: [ crbc: "#ffe0b2", crbc2: "#f57c00", crtc: "#000000", htc: "#ffffff", hbc: "#f57c00", rtc: "#000000", rbc: "#ffddaa" ],
		Brown: [ crbc: "#d7ccc8", crbc2: "#5d4037", crtc: "#000000", htc: "#ffffff", hbc: "#5d4037", rtc: "#000000", rbc: "#dacea4" ],
        Purple: [ crbc: "#e1bee7", crbc2: "#7b1fa2", crtc: "#000000", htc: "#ffffff", hbc: "#7b1fa2", rtc: "#000000", rbc: "#d9b8db" ],
		Pink: [ crbc: "#fce4ec", crbc2: "#f8bbd0", crtc: "#333333", htc: "#333333", hbc: "#f48fb1", rtc: "#333333", rbc: "#c6dee2" ],
		Mono: [crbc:"#E0E0E0", crbc2:"#393939", crtc:"#212121", htc:"#FFFFFF", hbc:"#4b4949", rtc:"#000000", rbc:"#d1d1d1"],
		]

	def myTheme = settings.theme.toString()
	
    def t = themes[myTheme] ?: themes.Blue // fallback to Blue if theme not found

    // Custom Row Properties
    app.updateSetting("crbc", [value: t.crbc,  type: "color"])
    app.updateSetting("crbc2", [value: t.crbc2, type: "color"])
    app.updateSetting("crtc", [value: t.crtc,  type: "color"])
    app.updateSetting("crts", [value: "100", type: "enum"])

    // Header
    app.updateSetting("hts", "100")
    app.updateSetting("htc", [value: t.htc, type: "color"])
    app.updateSetting("hbc", [value: t.hbc, type: "color"])
    app.updateSetting("hbo", "1")

    // Row
    app.updateSetting("rts", "90")
    app.updateSetting("rtc", [value: t.rtc, type: "color"])
    app.updateSetting("rbc", [value: t.rbc, type: "color"])
    app.updateSetting("rbo", "1")
	
	// Header Properties
    app.updateSetting("htc", [value: t.htc, type: "color"])
    app.updateSetting("hbc", [value: t.hbc, type: "color"])

    // Row Properties
    app.updateSetting("rtc", [value: t.rtc, type: "color"])
    app.updateSetting("rbc", [value: t.rbc, type: "color"])

    // Highlight Colors
    app.updateSetting("rbs",  [value: "#DCE775", type: "color"]) // selected row
    
	// Common Properties (general + highlight + borders)
    [tvp: "3", thp: "5", hts: "100", hbo: "1", rts: "90", rbo: "1", its1: "80", its2: "80", its3: "80", ita1: "Center", ita2: "Center", ita3: "Center", bwo: "4", bwi: "2", tpad: "3", highlightSelectedRows: "True" ].each { k, v -> app.updateSetting(k, [value: v.toString(), type: "enum"]) }
   
    // Borders
    app.updateSetting("bc", [value: "#000000", type: "color"]) // black border
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
//**************  Start Device Renaming and Variable Substitution
//**************
//*******************************************************************************************************************************************************************************************

// Replace %var11% - %var105% variables that may be found in device/separator strings or device names with actual values.
def replaceVarsInString(str) {
    if (!str) return ""
    def dpMap = ["var": 0, "Var": 1, "vAr": 2, "vaR": 3]
    def pattern = /%([vV][aA][rR])(\d+)%/

    return str.replaceAll(pattern) { fullMatch, varLabel, varNum ->
        def dp = dpMap[varLabel]
        if (dp == null) return fullMatch // Unrecognized casing
        def i = varNum.toInteger()
        def replacement = getVariableText(i, dp)

        //log.info("REPLACEMENT for %${varLabel}${varNum}% is: $replacement with DP=$dp")
        return replacement ?: fullMatch // fallback to original if null/empty
    }
}

// Helper Function to replace variables with their attribute values.
String getVariableText(var, dp) {
    //log.info("getVariableText(): $var with dp: $dp")
    def dev, attrIndex
    def varStr = var.toString()
    def myValue = null

    if (var < 11) {
        dev = var
        attrIndex = 0
    } else {
        attrIndex = varStr[-1] as Integer  // Last digit
        dev = varStr[0..-2] as Integer     // All but last digit
    }

    // Check device attribute
    if (settings["variableSource${dev}"] == "Device Attribute" &&
        settings["myDevice${dev}"] != null &&
        settings["myAttribute${var}"] != null) {
        myValue = settings["myDevice${dev}"]?.currentValue(settings["myAttribute${var}"])
        
        def myStateMap = new JsonSlurper().parseText(deviceStateMap)
        def key = myValue.toString()
        if (myStateMap?.containsKey(key)) { myValue = myStateMap[key] }   
        if (isLogDebug) log.debug("getVariableText(Device - $var) - Attribute: ${settings["myAttribute${var}"]} = $myValue")
    }
    
    // Check Hub Variable
    if (settings["variableSource${dev}"] == "Hub Variable" &&
        settings["myHubVariable${var}"] != null) {
        def myMap = getGlobalVar(settings["myHubVariable${var}"])
        myValue = myMap?.value?.toString() ?: invalidAttribute.toString()
        if (isLogDebug) log.debug("getVariableText(Hub Variable - $var) - Hub Var: ${settings["myHubVariable${var}"]} = $myValue")
    }

    // Check Hub Property
    if (settings["variableSource${dev}"] == "Hub Property" &&
        settings["myHubProperty${var}"] != null) {
        myValue = getHubProperty(settings["myHubProperty${var}"])
        if (isLogDebug) log.debug("getVariableText(Hub Property - $var) - Hub Property: ${settings["myHubProperty${var}"]} = $myValue")
    }

    if (myValue == null) return invalidAttribute.toString()
    def str = myValue.toString().trim()

    try {
        def num = new BigDecimal(str)
        def rounded = num.setScale(dp, BigDecimal.ROUND_HALF_UP)
        //log.info("Returning: $rounded")
        return rounded.toPlainString()
    } catch (e) {
        //log.info("Error Found: $e")
        //log.info("Returning: $str")
        return capitalizeStrings.toString() == "True" ? str.capitalize() : str
    }
}

//Perform any Device Renaming requested using the Device Name Modification Fields. Fill out an %varXX% variables.
def getShortName(myDevice){
	//log.info("Receiving Name: $myDevice")
	def shortName = myDevice
    
    //Replaces any undesireable characters in the devicename - Case Sensitive
    //Goes through each of the Device Rename Fields 1 - 10 and performs the required search and replace functions.
    for (int i = 1; i <= myDeviceRenameCount.toInteger(); i++) {
        def search = this."mySearchText$i"
        def replace = this."myReplaceText$i"
        if (replace == null || replace == "?") { this."myReplaceText$i" = ""; replace = "" }
        if (search != null && search != "?") { shortName = shortName.replace(search, replace) }
    }
    //Replaces any %varX% variables found.
    shortName = toHTML(replaceVarsInString(shortName))
	//log.info("returning shortName: $shortName")
    return shortName
}

//Retrieves Hub Properties such as sunrise, sunset, hsmStatus etc.
def getHubProperty(hubPropertyName) {
    if (isLogTrace) log.info("<b>getHubProperty() Received $hubPropertyName</b>")
    myDateTimeIndex = 0
    def outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    def sunrise = getTodaysSunrise()
    def sunset = getTodaysSunset()
    def currentTime = new Date()
    def myDate = outputFormat.format(currentTime)
    
    switch (hubPropertyName) {
        case "sunrise":
            return sunrise.format('HH:mm a')
            break
        case "sunrise1":
            return sunrise.format('HH:MM')
            break
        case "sunrise2":
            return sunrise.format('h:mm a')
            break
        case "sunset":
            return sunset.format('HH:mm a')
            break
        case "sunset1":
            return sunset.format('HH:MM')
            break
        case "sunset2":
            return sunset.format('h:mm a')
            break
        case "hubName":
            return location.hub
            break
        case "currentMode":
            return location.properties.currentMode.toString()
            break
        case "hsmStatus":
            return location.hsmStatus
            break
        case "firmwareVersionString":
            return location.hub.firmwareVersionString.toString()
            break
        case "uptime":
            long myUptime = location.hub.uptime
            uptime = convertSecondsToDHMS(myUptime, false)
            return uptime
            break
        case "timeZone":
            def timeZone = location.timeZone
            def myTimeZoneAbbreviation = timeZone.getDisplayName(false, TimeZone.SHORT)
            return myTimeZoneAbbreviation
            break
        case "daylightSavingsTime":
            def timeZone = location.timeZone
            boolean DST = timeZone.inDaylightTime(new Date())
            return DST.toString()
            break
        case "currentTime":
			def dateFormat = new SimpleDateFormat("HH:mm a")
            return dateFormat.format(currentTime)        
			break
        case "currentTime1":
            def dateFormat = new SimpleDateFormat("HH:MM")
            return dateFormat.format(currentTime)        
            break
        case "currentTime2":
        	def dateFormat = new SimpleDateFormat("h:mm a")
            return dateFormat.format(currentTime)        
            break
        case "default":
            log.error("getHubProperty: getHubProperty was not found. Returning 'EmptyHubProperty' with dataType 'String'")
            return "EmptyHubProperty"
            break
    }
    return "EmptyHubProperty"
}

//*******************************************************************************************************************************************************************************************
//**************
//**************  End Device Renaming and Variable Substitution
//**************
//*******************************************************************************************************************************************************************************************






//*******************************************************************************************************************************************************************************************
//**************
//**************  Device Data Collection and Preparation Functions
//**************
//*******************************************************************************************************************************************************************************************

// Gets the state of the various lights that are being tracked and puts them into a JSON format for inclusion with the script 
def getJSON() {
    if (isLogTrace) log.trace("<b>Entering: GetJSON</b>")
    def timeStart = now()
    
    // List to hold device attribute data
    def deviceAttributesList = []
	def eventData = [:]
	
	//Gets the device info that does not change, such as name, type, ID etc and saves it to state so we only look it up once.
	cacheDeviceInfo()
	
    // Iterate through each device
    myDevices.each { device ->
		// Use LinkedHashMap to maintain the order of fields
        def deviceData = new LinkedHashMap()
		def deviceID = device.getId().toString()
		deviceData.put("ID", device.getId())
		
		//Get the cached version of the name which may be short from device name modification
		deviceData.put("name", state.deviceList.find { it.ID == deviceID }?.name)
		def mySwitch = device.currentValue("switch")
		deviceData.put("switch", mySwitch)
		
        //Get the device Type from cache so we don't have to calculate it every time. Makes the code on this end simpler.
		deviceType = state.deviceList.find { it.ID == deviceID }?.type
		deviceData.put("type", deviceType)  
        		
		def deviceTypeHandlers = [
			1 : { d, dd -> def s = d.currentValue("switch"); dd.icon = getIcon(1, s)?.icon; dd.cl = getIcon(1, s)?.class },
			2 : { d, dd -> def s = d.currentValue("switch"); dd.level = d.currentValue("level")?.toInteger() ?: 100; dd.icon = getIcon(2, s)?.icon; dd.cl = getIcon(2, s)?.class },
			3 : { d, dd -> def s = d.currentValue("switch"); dd.level = d.currentValue("level")?.toInteger() ?: 100; dd.CT = d.currentValue("colorTemperature")?.toInteger() ?: 2000; dd.icon = getIcon(3, s)?.icon; dd.cl = getIcon(3, s)?.class },
			4 : { d, dd -> def s = d.currentValue("switch"); dd.level = d.currentValue("level")?.toInteger() ?: 100; dd.CT = d.currentValue("colorTemperature")?.toInteger() ?: 2000; def hsv = [hue: d.currentValue("hue") ?: 100, saturation: d.currentValue("saturation") ?: 100, value: dd.level]; dd.color = getHEXfromHSV(hsv); dd.icon = getIcon(4, s)?.icon; dd.cl = getIcon(4, s)?.class },
			5 : { d, dd -> def s = d.currentValue("switch"); dd.level = d.currentValue("level")?.toInteger() ?: 100; dd.CT = d.currentValue("colorTemperature")?.toInteger() ?: 2000; def hsv = [hue: d.currentValue("hue") ?: 100, saturation: d.currentValue("saturation") ?: 100, value: dd.level]; dd.color = getHEXfromHSV(hsv); dd.colorMode = d.currentValue("colorMode"); dd.icon = getIcon(5, s)?.icon; dd.cl = getIcon(5, s)?.class },
			10: { d, dd -> def v = d.currentValue("valve"); def s = (v == "open") ? "on" : "off"; dd.switch = s; dd.icon = getIcon(10, s)?.icon; dd.cl = getIcon(10, s)?.class },
			11: { d, dd -> def l = d.currentValue("lock"); def s = (l == "locked") ? "on" : "off"; dd.switch = s; dd.icon = getIcon(11, s)?.icon; dd.cl = getIcon(11, s)?.class },
			12: { d, dd -> def sp = d.currentValue("speed"); def s = (sp == "off") ? "off" : "on"; dd.speed = sp; dd.switch = s; dd.icon = getIcon(12, s)?.icon; dd.cl = getIcon(12, sp)?.class },
			13: { d, dd -> def dr = d.currentValue("door"); def s = (dr == "closed") ? "on" : "off"; dd.door = dr; dd.switch = s; dd.icon = getIcon(13, dr)?.icon; dd.cl = getIcon(13, dr)?.class },
			14: { d, dd -> def st = d.currentValue("windowShade"); def pos = d.currentValue("position"); def s = (st == "closed") ? "off" : "on"; dd.windowShade = st; dd.position = pos; dd.switch = s; dd.icon = getIcon(14, st)?.icon; dd.cl = getIcon(14, st)?.class },
			15: { d, dd -> def st = d.currentValue("windowShade"); def s = (st == "closed") ? "off" : "on"; dd.position = d.currentValue("position"); dd.tilt = Math.round(d.currentValue("tilt") * 0.9); dd.switch = s; dd.icon = getIcon(15, st)?.icon; dd.cl = getIcon(15, st)?.class },
			16: { d, dd -> def m = d.currentValue("mute"); def s = (m == "muted") ? "off" : "on"; dd.switch = s; dd.volume = d.currentValue("volume"); dd.icon = getIcon(16, s)?.icon; dd.cl = getIcon(16, s)?.class }
		]

		def handler = deviceTypeHandlers[deviceType]
		if (handler) { handler(device, deviceData) }
		deviceDetails = getDeviceInfo(device, deviceData.get("type") )
						
		//Gather event information if needed for either of the two columns.
		if (hideColumn7 == false ) deviceData.put("i1", deviceDetails."${info1Source}")
		if (hideColumn8 == false ) deviceData.put("i2", deviceDetails."${info2Source}")
		if (hideColumn9 == false ) deviceData.put("i3", deviceDetails."${info3Source}")
			
        // Add device data to the list
        deviceAttributesList << deviceData
		//log.info("Device: $deviceData.name is type: $deviceData.type and data is: $deviceData")
    }
	
	def sensorConfigs = [
		31: [list: myContacts, attr: "contact", iconAttrVal: { it -> it }, condition: { val -> onlyOpenContacts == "False" || (onlyOpenContacts == "True" && val == "open") }],
		32: [list: myTemps, attr: "temperature", iconAttrVal: { "temp" }, processVal: { val -> float t = val as float; if (tempDecimalPlaces == "0 Decimal Places") return t.round(0).toInteger().toString() + 
				tempUnits; if (tempDecimalPlaces == "1 Decimal Place") return t.round(1).toString() + tempUnits; return t.toString() + tempUnits }, condition: { val -> if (onlyReportOutsideRange == "False") 
				return true; float t = val as float; return (t < minTemp.toInteger() || t > maxTemp.toInteger()) }],
		33: [list: myLeaks, attr: "water", iconAttrVal: { it -> it }, condition: { val -> onlyWetSensors == "False" || (onlyWetSensors == "True" && val == "wet") }],
        34: [list: myMotion, attr: "motion", iconAttrVal: { it -> it }, condition: { val -> onlyActiveMotionSensors == "False" || (onlyActiveMotionSensors == "True" && val == "active") }],
        35: [list: myPresence, attr: "presence", iconAttrVal: { it -> it }, condition: { val -> onlyPresentPresenceSensors == "False" || (onlyPresentPresenceSensors == "True" && val == "present") }],
        36: [list: mySmoke, attr: "smoke", iconAttrVal: { it -> it }, condition: { val -> onlyDetectedSmoke == "False" || (onlyDetectedSmoke == "True" && val == "detected") }],
        37: [list: myCarbonMonoxide, attr: "carbonMonoxide", iconAttrVal: { it -> it }, condition: { val -> onlyDetectedCarbonMonoxide == "False" || (onlyDetectedCarbonMonoxide == "True" && val == "detected") }]
	]
	//Loops through the sensor types
	sensorConfigs.each { type, cfg ->
		cfg.list.each { device ->
			def deviceData = new LinkedHashMap()
			def deviceID = device.getId().toString()
			deviceData.put("ID", deviceID.toString())
			deviceData.put("name", state.deviceList.find { it.ID == deviceID }?.name)
			deviceData.put("type", type)

			def rawVal = device.currentValue(cfg.attr)
			def displayVal = cfg.processVal ? cfg.processVal(rawVal) : rawVal

			deviceData.put("switch", displayVal)
			def iconInfo = getIcon(type, cfg.iconAttrVal(rawVal))
			deviceData.put("icon", iconInfo?.icon)
			deviceData.put("cl", iconInfo?.class)
			
			def meetsCondition = cfg.condition ? cfg.condition(rawVal) : true

            deviceAttributesList << deviceData
            
            def deviceDetails = getDeviceInfo(device, type)
			if (!hideColumn7) deviceData.put("i1", deviceDetails."${info1Source}")
			if (!hideColumn8) deviceData.put("i2", deviceDetails."${info2Source}")
			if (!hideColumn9) deviceData.put("i3", deviceDetails."${info3Source}")
		}
	}
		
	// Gather the data for the custom rows and separators
	if (customRowCount.toInteger() > 0 && isCustomSort == "true") {		
		(1..customRowCount.toInteger()).each { i ->
			// Skip if the row type is Disabled
			def rowType = settings["customRowType$i"]?.toString()
            if (rowType == "Disabled") return
            
            // Use LinkedHashMap to maintain the order of fields
			def deviceData = new LinkedHashMap()
			def deviceID = ( 0 - i ) 
			def myType
			deviceData.put("ID", "$deviceID") // We use a negative deviceID for Custom Rows because they can conflict with very low numbered devices.

            if (rowType == "Separator Row") {
                myType = 51 // Separator Row
                deviceData.put("icon", getIcon(myType, "separatorRow")?.icon)
                deviceData.put("cl", getIcon(myType, "separatorRow")?.class)
            } else if (rowType == "Device Row") {
                myType = 52 // Device Row
                deviceData.put("icon", getIcon(myType, "deviceRow")?.icon)
                deviceData.put("cl", getIcon(myType, "deviceRow")?.class)
            } else if (rowType == "iFrame Row") {
                myType = 53 // iFrame Row
                deviceData.put("icon", getIcon(myType, "iFrameRow")?.icon)
                deviceData.put("cl", getIcon(myType, "iFrameRow")?.class)
            }

			//Put the data into the cells that are expected by the applet.
			deviceData.put("type", myType)
			deviceData.put("name", toHTML(replaceVarsInString(settings["myNameText$i"]?.toString())))
			if (rowType == "iFrame Row") { 
    			def srcUrl = settings["myStateText$i"]?.toString() ?: ""
                def myHeight = settings["myIFrameHeight$i"]
                def myURL = "[iframe src='${srcUrl}' style='height:${myHeight}px; width:100%; border:none; overflow:hidden;'][/iframe]"		//Note: You cannot use percentages for height because no height is defined. So we must use pixels.
    			deviceData.put("switch", toHTML(replaceVarsInString(myURL)))
			}
            else deviceData.put("switch", toHTML(replaceVarsInString(settings["myStateText$i"]?.toString()))) 
			if (!hideColumn5) deviceData.put("level", toHTML(replaceVarsInString(settings["myControlABText$i"]?.toString())))
            if (!hideColumn6) deviceData.put("CT", toHTML(replaceVarsInString(settings["myControlCText$i"]?.toString())))
			if (!hideColumn7) deviceData.put("i1", toHTML(replaceVarsInString(settings["myInfoAText$i"]?.toString())))
			if (!hideColumn8) deviceData.put("i2", toHTML(replaceVarsInString(settings["myInfoBText$i"]?.toString())))
			if (!hideColumn9) deviceData.put("i3", toHTML(replaceVarsInString(settings["myInfoCText$i"]?.toString())))
			deviceAttributesList << deviceData
		}
	}
	
	// Convert the list of device attributes to JSON format
    def compactJSON = JsonOutput.toJson(deviceAttributesList)
			
	//Save the compact JSON. This is the version that is collected by the client.
	state.JSON = compactJSON
	
	//If we are using a Custom Sort we have to calculate the new Sort Order and save it to state.JSON
	if (isCustomSort == "true"){
		// Parse JSON into lists
		def slurper = new JsonSlurper()
		def list1 = slurper.parseText(state?.JSON)
		def list2 = slurper.parseText(state.customSortOrder)
		
		// Create a map from JSON2 for quick lookup
		def rowMap = list2.collectEntries { [(it.ID): it.row] }

		// Merge data
		def mergedList = list1.collect { item ->
			if (rowMap.containsKey(item.ID)) {
				item.row = rowMap[item.ID]  // Add row field
			}
			return item
		}
		state.JSON = JsonOutput.prettyPrint(JsonOutput.toJson(mergedList))
		//if (isLogDebug) 
        //log.debug  ("Merged List: " + JsonOutput.prettyPrint(JsonOutput.toJson(mergedList)) )
	}
    def timeElapsed = now() - timeStart
    if (isLogTrace) log.trace ("Leaving: getJSON " + timeElapsed/1000 + " seconds.")
}

// Return the appropriate icon and class to match the type and deviceState
def getIcon(type, deviceState) {
	def icons = [
        1  : [on: [icon: "toggle_on", class: "on"], off: [icon: "toggle_off", class: "off"]],
        2  : [on: [icon: "lightbulb", class: "on"], off: [icon: "light_off", class: "off"]],
        3  : [on: [icon: "lightbulb", class: "on"], off: [icon: "light_off", class: "off"]],
        4  : [on: [icon: "lightbulb", class: "on"], off: [icon: "light_off", class: "off"]],
        5  : [on: [icon: "lightbulb", class: "on"], off: [icon: "light_off", class: "off"]],
        10 : [on: [icon: "water_pump", class: "on"], off: [icon: "valve", class: "off"]],
        11 : [on: [icon: "lock", class: "good"], off: [icon: "lock_open", class: "bad"]],
        12 : [on: [icon: "mode_fan", class: "spinning"], off: [icon: "mode_fan_off", class: "off"], low: [icon: "mode_fan", class: "spin-low"], medium: [icon: "mode_fan", class: "spin-medium"], high: [icon: "mode_fan", class: "spin-high"]],
        13 : [closed: [icon: "garage_door", class: "good"], open: [icon: "garage", class: "warn"], opening: [icon: "arrow_upward", class: "blinking"], closing: [icon: "arrow_downward", class: "blinking"]],
        14 : [open: [icon: "window_closed", class: "on"], closed: [icon: "roller_shades_closed", class: "off"], opening: [icon: "arrow_upward", class: "blinking"], closing: [icon: "arrow_downward", class: "blinking"], 'partially open': [icon: "roller_shades", class: "partial"]],
        15 : [open: [icon: "window_closed", class: "on"], closed: [icon: "blinds_closed", class: "off"], opening: [icon: "arrow_upward", class: "blinking"], closing: [icon: "arrow_downward", class: "blinking"], 'partially open': [icon: "blinds", class: "on"]],
        16 : [on: [icon: "volume_up", class: "on"], off: [icon: "volume_off", class: "off"]],
        20 : [on: [icon: "mode_cool", class: "cooling"], off: [icon: "mode_cool_off", class: "inactive"]],
        21 : [on: [icon: "mode_heat", class: "heating"], off: [icon: "mode_heat_off", class: "inactive"]],
        22 : [on: [icon: "mode_dual", class: "dual-mode"], off: [icon: "mode_dual_off", class: "inactive"]],
        23 : [on: [icon: "emergency_heat", class: "emergency"], off: [icon: "emergency_heat", class: "inactive"]],
        // Sensors start at 31
        31 : [open: [icon: "expand_content", class: "warn"], closed: [icon: "collapse_content", class: "off"]],
		32 : [temp: [icon: "device_thermostat", class: "off"]],
		33 : [wet: [icon: "water_drop", class: "bad"], dry: [icon: "format_color_reset", class: "off"] ],
        34 : [active: [icon: "detection_and_zone", class: "blinking_orange"], inactive: [icon: "zone_person_idle", class: "off"] ],
        35 : [present: [icon: "home", class: "good"], 'not present': [icon: "directions_car", class: "warn"] ],
        36 : [clear: [icon: "detector_smoke", class: "off"], detected: [icon: "detector_smoke", class: "bad"], tested: [icon: "detector_status", class: "good"] ],
        37 : [clear: [icon: "detector_co", class: "off"], detected: [icon: "detector_co", class: "bad"], tested: [icon: "detector_status", class: "good"] ],
		39 : [battery: [icon: "battery_android_4", class: "off"] ],
		//Custom Devices start at 51
        51 : [separatorRow: [icon: "atr", class: "group"] ],
		52 : [deviceRow: [icon: "info", class: "off"] ],
        53 : [iFrameRow: [icon: "iframe", class: "off"] ]
    ]

    // Retrieve the entry for the given type and deviceState
    def result = icons[type]?.get(deviceState)
    
    // Return the result if found, otherwise return a default structure
	//log.info ("Returning: $result")
    return result ?: [icon: "error", class: "warn"]
}

//Check to see if a given infoSource is in use across all three possibilities
def isInfoSource(source){
	//log.info("Request: $source  --  info1Source: $info1Source - info2Source: $info2Source - info3Source: $info3Source")
	def match = false
	if (info1Source == source) match = true
	if (info2Source == source) match = true
	if (info3Source == source) match = true
	//if (match == true) log.info("Match for source: $source")
	return match
}

//Gets information about the lastActive, lastInactive etc and put it into a map. Data from selected fields will be mapped into the Info columns.
def getDeviceInfo(device, type){
	def lastActiveEvent
	def lastInactiveEvent
	def lastActive
	def lastInactive
	def lastActiveInstant
	def lastInactiveInstant
	def lastActiveDuration
	def lastSeen
	def lastSeenElapsed
	
	def roomName
	def colorName
	def colorMode
	def power
	def healthStatus
	def energy
	def network
	def colorTemperature

	def deviceTypeName
	def lastActivity
	def battery
	def temperature
	def ID = device?.getId()
	def deviceName = device.getLabel()
   
	//Get the appropriate piece of information only if it has been requested.
	if (isInfoSource("roomName")) roomName = device?.getRoomName()
	if (isInfoSource("colorName")) colorName = device?.currentValue("colorName")
	if (isInfoSource("colorMode")) colorMode = device?.currentValue("colorMode")
	if (isInfoSource("power")) power = device?.currentValue("power")
	if (isInfoSource("healthStatus")) healthStatus = device?.currentValue("healthStatus")
	if (isInfoSource("energy")) energy = device?.currentValue("energy")
	if (isInfoSource("network")) network = getNetworkType(device?.getDeviceNetworkId())
	if (isInfoSource("deviceTypeName")) deviceTypeName = getDeviceTypeInfo(type)
	if (isInfoSource("battery") && device.hasCapability("Battery") ) battery = device?.currentValue("battery") + "%"
	if (isInfoSource("colorTemperature") && device.hasCapability("ColorTemperature") ) colorTemperature = device?.currentValue("colorTemperature") + "°K"
	if (isInfoSource("temperature") && device.hasCapability("TemperatureMeasurement") ) {
		myTemp = device?.currentValue("temperature")
		def roundedValue = Math.round(myTemp) // Returns a long
		temperature = roundedValue.toInteger().toString() + tempUnits
	}
		
	//log.info("deviceName: $deviceName  type: $type")
	if (isInfoSource("lastActive") || isInfoSource("lastActiveDuration") || isInfoSource("lastInactive") || isInfoSource("lastInactiveDuration") ) {
	
		switch(type){
			case [1,2,3,4,5]:  //Lights
				lastActiveEvent = device.events(max: 500) .findAll { it.name == "switch" && it?.value == "on" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				lastInactiveEvent = device.events(max: 500) .findAll { it.name == "switch" && it?.value == "off" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				break
			case 10: //Valve
				lastActiveEvent = device.events(max: 500) .findAll { it.name == "valve" && it?.value == "open" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				lastInactiveEvent = device.events(max: 500) .findAll { it.name == "valve" && it?.value == "closed" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				break
			case 11: //Lock
				lastActiveEvent = device.events(max: 500) .findAll { it.name == "lock" && it?.value == "locked" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				lastInactiveEvent = device.events(max: 500) .findAll { it.name == "lock" && it?.value == "unlocked" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				break
			case 12: //Fan
				lastActiveEvent = device.events(max: 500) .findAll { it.name == "speed" && it?.value != "off" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				lastInactiveEvent = device.events(max: 500) .findAll { it.name == "speed" && it?.value == "off" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				break
			case 13: //Garage Door
				lastActiveEvent = device.events(max: 500) .findAll { it.name == "door" && it?.value == "closed" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				lastInactiveEvent = device.events(max: 500) .findAll { it.name == "door" && it?.value != "closed" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				break
			case [14,15]: //Shades and Blinds
				lastActiveEvent = device.events(max: 500) .findAll { it.name == "windowShade" && it?.value == "open" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				lastInactiveEvent = device.events(max: 500) .findAll { it.name == "windowShade" && it?.value != "open" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				break
			case 31: //Contact Sensor
				lastActiveEvent = device.events(max: 500) .findAll { it.name == "contact" && it?.value == "open" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				lastInactiveEvent = device.events(max: 500) .findAll { it.name == "contact" && it?.value == "closed" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				break
			case 33: //Water Sensor
				lastActiveEvent = device.events(max: 500) .findAll { it.name == "water" && it?.value == "wet" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				lastInactiveEvent = device.events(max: 500) .findAll { it.name == "water" && it?.value == "dry" } .sort { -it.date.time } ?.with { it.isEmpty() ? null : it.first() }
				break
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
			durations = getDuration(lastActiveInstant, lastInactiveInstant)
			lastActiveDuration = durations.lastActiveDuration
			lastInactiveDuration = durations.lastInactiveDuration
		}
	}
		
	if (isInfoSource("lastSeen") || isInfoSource("lastSeenElapsed") ) {
		lastActivity = device?.getLastActivity()
		if (lastActivity != null) {
			def timestamp = lastActivity.time
			lastSeen = formatTime(timestamp, defaultDateTimeFormat.toInteger() ?: 3)
			def durations = getDuration(timestamp, now())
			lastSeenElapsed = durations.lastActiveDuration
		}
	}
	
	def result = [lastActive: lastActive, lastInactive: lastInactive, lastInactiveInstant: lastInactiveInstant, lastActiveInstant: lastActiveInstant, lastActiveDuration: lastActiveDuration, 
				  lastInactiveDuration: lastInactiveDuration, roomName: roomName, colorName: colorName, colorMode: colorMode, power: power, healthStatus: healthStatus, 
				  energy: energy, ID: ID, network: network, deviceTypeName: deviceTypeName, lastSeen: lastSeen, lastSeenElapsed: lastSeenElapsed, battery: battery, temperature: temperature, colorTemperature: colorTemperature ].collectEntries { key, value -> [key, value != null ? value : invalidAttribute.toString()] }
	
	//log.info("Returning map: $result")
    return result
}

// Function to determine network type based on DNI length
def getNetworkType(dni) {
    def networkTypes = [2: "Z-Wave", 4: "Zigbee", 8: "LAN", 36: "Virtual" ]
    return networkTypes[dni?.length()] ?: "Other"
}

//Can return either a type name or a type number
def getDeviceTypeInfo(input) {
    def maps = createDeviceTypeMap()
    def typeMap = maps.typeMap
    def nameToNumberMap = maps.nameToNumberMap

    if (input instanceof Number) { return typeMap[input] ?: "Unknown device type number" } 
	else if (input instanceof String) { return nameToNumberMap[input] ?: "Unknown device type name" } 
	else { return "Invalid input" }
}

// Takes a JSON list of devices and changes, and applies them.
def applyChangesToDevices(changes) {
    if (isLogTrace) log.trace("<b>Entering: applyChangesToDevices</b>")
    if (isLogDeviceInfo) log.debug("Changes are: $changes")

    // Define a map of actions
    def commandMap = [ 'switch' : { ID, type, newValue -> handleSwitch(ID, type, newValue) }, 'level' : { ID, _, newValue -> ID.setLevel(newValue, 0.4) }, 'volume' : { ID, _, newValue -> ID.setVolume(newValue) },
        'position': { ID, _, newValue -> ID.setPosition(newValue) }, 'tilt' : { ID, _, newValue -> ID.setTiltLevel(Math.round(newValue * (100 / 90))) }, 'speed' : { ID, _, newValue -> ID.setSpeed(newValue) },
        'name'    : { ID, _, newValue -> ID.setLabel(newValue) }, 'CT' : { ID, _, newValue -> ID.setColorTemperature(newValue, null, 0.2) }, 'color' : { ID, _, newValue -> setDeviceColor(ID, newValue) }
    ]

    // Iterate over changes
    changes.each { change ->
        def device = findDeviceById(change.ID)
        if (device) {
            def type = change.type
            change.changes.each { key, values ->
                def newValue = values[1] // Get the new value for each property
                def command = commandMap[key]

                if (command) { command(device, type, newValue) } 
				else { if (isLogDebug) log.warn("Unhandled change: $key") }
            }
        }
    }
}

// Handles switch-type actions for different device types
def handleSwitch(device, type, newValue) {
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
        if (keys.contains(type as Integer)) {
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

// Iterate over each device in myDevices and check if ID matches. If it does match return that device.
def findDeviceById(ID) {
    def foundDevice = null  // Variable to store the found device
    myDevices.each { device ->
        if (device.getId() == ID) {
            foundDevice = device
            return foundDevice
        }
    }
    return foundDevice
}

//*******************************************************************************************************************************************************************************************
//**************
//**************  Time and Date Related Functions
//**************
//*******************************************************************************************************************************************************************************************

//This is derived from the formatTime function used in Grid but in this case the only Time format received is from Events which are always "java.sql.Timestamp" or from lastActivity which are of type Long.
//Receives a time as an event timestamp and converts it into one of many alternate time formats.
def formatTime(timeValue, int format) {
	def isLogDateTime = false
	if (isLogDateTime) log.info("<b>formatTime: Time received is: $timeValue and requesting format: $format</b>")
    def myLongTime
    
    // N/A means the requested attribute was not found.
    if (timeValue == "N/A") return 0
	
	def myClass = getObjectClassName(timeValue)
	if (myClass == "java.sql.Timestamp" ) myLongTime = timeValue.getTime()
	else myLongTime = timeValue
	
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
	//if (isLogTrace) log.trace("<b>getDuration: Received $lastActiveEvent, $lastInactiveEvent</b>")
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
//**************  End of Time and Date Related Functions
//**************
//*******************************************************************************************************************************************************************************************



//*******************************************************************************************************************************************************************************************
//**************
//**************  Compile Time Functions
//**************
//*******************************************************************************************************************************************************************************************

//Compress the fixed components text output and generate the version that will be used by the browser.
def compile(){
	//Assign the device types
	try{
		if (isLogTrace) log.trace("<b>Entering: Compile</b>")
		if (isLogDebug) log.debug("Running Compile")
			
		def html1 = myHTML()
		def content = condense(html1)
		
		def localContent
		def cloudContent

		//Sets the size of the controls within the SmartGrid
		content = content.replace('#controlSize#', controlSize.toString() )
		
		//The AppID is used as a unique key for saving items in local and session storage so that preferences are tied to the each unique SmartGrid
		state.AppID = state.accessToken.toString()[-4..-1]
		content = content.replace('#AppID#', state.AppID )
		
		//Table Horizontal Alignment
		if (ha == "Stretch") content = content.replace('#ha#', "stretch" )
		if (ha == "Left") content = content.replace('#ha#', "flex-start" )
		if (ha == "Center") content = content.replace('#ha#', "center" )
		if (ha == "Right") content = content.replace('#ha#', "flex-end" )

		//Table Padding
		content = content.replace('#tpad#', tpad )
		content = content.replace('#thp#', thp )
		content = content.replace('#tmt#', tmt )	//This is actually a margin
		
        //Internal Vertical Padding
		mytvp = (tvp.toInteger() + tvpm.toFloat()).toString()
		content = content.replace('#tvp#', mytvp )
        
		//Borders & Padding
		content = content.replace('#bc#', bc )
		content = content.replace('#bwo#', bwo )
		content = content.replace('#bwi#', bwi )
        
        // Configure the colspan required for the iFrame rows depending on what other columns are hidden or visible
        def columns = [ hideColumn1, hideColumn2, hideColumn3, hideColumn4, hideColumn5, hideColumn6, hideColumn7, hideColumn8, hideColumn9, hideColumn10, hideColumn11, hideColumn12 ]
		def visibleColumnCount = columns.count { !(it == true) }
		def colspan = visibleColumnCount - ( (hideColumn1 != true) ? 1 : 0 ) - ( (hideColumn2 != true) ? 1 : 0 ) - ( (hideColumn11 != true) ? 1 : 0 ) - ( (hideColumn12 != true) ? 1 : 0 )
        if (isLogDebug) log.debug "iFrame Row Column Span is: ${colspan}"
        content = content.replace('#iFrameColspan#', toHTML(colspan.toString()) )
        
		//Column Headers
		content = content.replace('#column3Header#', toHTML(column3Header) )	// Column 3 header text
		content = content.replace('#column5Header#', toHTML(column5Header) )	// Column 5 header text
		content = content.replace('#column6Header#', toHTML(column6Header) )	// Column 6 header text

		//Forced Column Widths - If the columns are marked as hidden change the width to zero.
		if (hideColumn3) { content = content.replace('#column3Width#', '0') } else { content = content.replace('#column3Width#', column3Width.toString()) }
        if (hideColumn4) { content = content.replace('#column4Width#', '0') } else { content = content.replace('#column4Width#', column4Width.toString()) }
		if (hideColumn5) { content = content.replace('#column5Width#', '0') } else { content = content.replace('#column5Width#', column5Width.toString()) }
		if (hideColumn6) { content = content.replace('#column6Width#', '0') } else { content = content.replace('#column6Width#', column6Width.toString()) }
		if ( highlightSelectedRows == "True" ) content = content.replace('#rbs#', rbs ) 
		else content = content.replace('#rbs#', "00000000" ) 
	
		//Custom Rows
		content = content.replace('#crbc#', crbc )
		content = content.replace('#crbc2#', crbc2 )
		content = content.replace('#crtc#', crtc )
		content = content.replace('#crts#', crts )

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
			content = content.replace('#tt#', toHTML(replaceVarsInString(tt)) )	// Title Text
			content = content.replace('#ts#', ts )	// Title Size
			content = content.replace('#tp#', tp )	// Title Padding
			content = content.replace('#ta#', ta )	// Title Alignment
			content = content.replace('#tc#', tc )	// Title Color
			def mytb = convertToHex8(tb, to.toFloat())  //Calculate the new color including the opacity.
			content = content.replace('#tb#', mytb )	// Title Background Color// Display Title or not
		}

		content = content.replace('#hts#', hts )	// Header Text Size
		content = content.replace('#htc#', htc )	// Header Text Color
		content = content.replace('#sortHeaderHintAZ#', sortHeaderHintAZ )
		content = content.replace('#sortHeaderHintZA#', sortHeaderHintZA )

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
		content = content.replace('#hideColumn3#', hideColumn3 ? 'none' : 'table-cell')
		content = content.replace('#hideColumn4#', hideColumn4 ? 'none' : 'table-cell')
		content = content.replace('#hideColumn5#', hideColumn5 ? 'none' : 'table-cell')
		content = content.replace('#hideColumn6#', hideColumn6 ? 'none' : 'table-cell')
		content = content.replace('#hideColumn7#', hideColumn7 ? 'none' : 'table-cell')
		content = content.replace('#hideColumn8#', hideColumn8 ? 'none' : 'table-cell')
		content = content.replace('#hideColumn9#', hideColumn9 ? 'none' : 'table-cell')
		content = content.replace('#hideColumn10#', hideColumn10 ? 'none' : 'table-cell')
		content = content.replace('#hideColumn11#', hideColumn11 ? 'none' : 'table-cell')
        content = content.replace('#hideColumn12#', hideColumn12 ? 'none' : 'table-cell')
		content = content.replace('#BrowserTitle#', myRemoteName)
	
		content = content.replace('#pollInterval#', (pollInterval.toInteger() * 1000).toString() )
		content = content.replace('#pollInterval#', (pollInterval.toInteger() * 1000).toString() )
		content = content.replace('#pollUpdateColorSuccess#', pollUpdateColorSuccess)
		content = content.replace('#pollUpdateColorFail#', pollUpdateColorFail)
		content = content.replace('#pollUpdateColorPending#', pollUpdateColorPending)
		
		content = content.replace('#pollUpdateDuration#', (pollUpdateDuration.toInteger() * 1000).toString() )
		content = content.replace('#commandTimeout#', (commandTimeout.toInteger() * 1000).toString() )
	
		if (isPolling == "Enabled") content = content.replace('#isPolling#', "true")
		if (isPolling == "Disabled") content = content.replace('#isPolling#', "false")

		content = content.replace('#shuttleColor#', shuttleColor)
		content = content.replace('#shuttleHeight#', shuttleHeight)
	
		//Drag & Drop - Custom Sort
		content = content.replace('#isDragDrop#', "$isDragDrop" )
		if (isDragDrop == true) {	//This is a boolean variable so it uses true\false.
			content = content.replace('#isDragDropCSS#',  "tbody tr {cursor: grab;} .dragging {opacity: 0.5;}" )
			content = content.replace('#isDragDrop#',  "true" )
		}
		else {
			content = content.replace('#isDragDropCSS#',  "tbody tr {cursor: normal;}" )
			content = content.replace('#isDragDrop#',  "false" )
		}
	
		if (isCustomSort == "true") content = content.replace('#isCustomSort#',  "true" )  //This is an ENUM so it uses a string compare.
		else content = content.replace('#isCustomSort#',  "false" )  //This is an ENUM so it uses a string compare.
		 		
		//Put the proper statement in for the Materials Font. It's done this way because the cleaning of comments catches the // in https://
		content = content.replace('#MaterialsFont#', "<link href='https://fonts.googleapis.com/icon?family=Material+Symbols+Outlined' rel='stylesheet'>")

		myWidth = ( (tilePreviewWidth.toFloat() * 210) - 10 )
		content = content.replace('#maxWidth#', myWidth.toString() )
    
    	//Experimental
		content = content.replace('#markTag#', markTag.toString() )
    	content = content.replace('#m1Tag#', m1Tag.toString() )
        content = content.replace('#m2Tag#', m2Tag.toString() )
        content = content.replace('#m3Tag#', m3Tag.toString() )
        content = content.replace('#m4Tag#', m4Tag.toString() )
        content = content.replace('#m5Tag#', m5Tag.toString() )
        content = content.replace('#deviceStateMap#', toHTML(deviceStateMap.toString()) )
        
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
		if (isLogTrace) log.trace("<b>Leaving: Compile</b>")
	}
    catch (Exception exception) { log.error("Function compile() - Exception is: $exception") }
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

//Determines the deviceInfo (type) for each device and saves it in state along with the deviceID and the short device name as these can only change at compile time. 
//This function only runs at compile() so it reduces the amount of work being performed by the Hub by caching these results vs calculating them each time.
def cacheDeviceInfo(){
	if (isLogTrace) log.trace(red("In Cache Device Info"))
	def myDeviceList = []
	myDevices.each { device ->
		def deviceInfo = new LinkedHashMap()
		deviceID = device.getId()
		deviceInfo.put("ID", deviceID)
		deviceInfo.put("name", getShortName(device.displayName))
		
		//Check if the device has "Switch" capability. Attributes are switch - ENUM ["on", "off"]
		if (device.hasCapability("Switch")) { deviceInfo.put("type", 1) }		       
        // Check if the device has "SwitchLevel" capability. Attributes are: level - NUMBER, unit:%
        if (device.hasCapability("SwitchLevel")) { deviceInfo.put("type", 2) }
		//If the device has ColorTemperature but NOT ColorControl then it is a CT only device. Attributes are: colorName - STRING colorTemperature - NUMBER, unit:°K
        if ( device.hasCapability("ColorTemperature") && !device.hasCapability("ColorControl") ) { deviceInfo.put("type", 3) }
		//If the device has ColorControl but NOT ColorTemperature then it is an RGB device. Attributes are: RGB - STRING color - STRING colorName - STRING hue - NUMBER saturation - NUMBER, unit:%
		if (device.hasCapability("ColorControl") && !device.hasCapability("ColorTemperature") ) { deviceInfo.put("type", 4) }
		//If the device has ColorControl AND ColorTemperature then it is a RGBW device. Attributes are: RGB - STRING color - STRING colorName - STRING hue - NUMBER saturation - NUMBER, unit:% +++++ colorTemperature - NUMBER, unit:°K
        if ( device.hasCapability("ColorControl") && device.hasCapability("ColorTemperature") ) { deviceInfo.put("type", 5) }
		//Check for valves - ENUM ["open", "closed"]
        if ( device.hasCapability("Valve") ) { deviceInfo.put("type", 10) }
		//Check for locks - states for lock are: ENUM ["locked", "unlocked with timeout", "unlocked", "unknown"]  //Only locked and unlocked are implemented.
        if ( device.hasCapability("Lock") ) { deviceInfo.put("type", 11) }
		//Check for Fans - States for speed are: ENUM ["low","medium-low","medium","medium-high","high","on","off","auto"]
        if ( device.hasCapability("FanControl") ) { deviceInfo.put("type", 12) }
		//Check for Garage Doors - States are: ENUM ["unknown", "open", "closing", "closed", "opening"]
        if ( device.hasCapability("GarageDoorControl") || device.hasCapability("DoorControl")) { deviceInfo.put("type", 13) }
		// Check for Shades and exclude blinds - States for windowShade are: ENUM ["opening", "partially open", "closed", "open", "closing", "unknown"]
		if (device.hasCapability("WindowShade") && !device.hasCapability("WindowBlind")) { deviceInfo.put("type", 14) }
		// Check for Blinds - States for windowBlind are: ENUM ["opening", "partially open", "closed", "open", "closing", "unknown"]
		if (device.hasCapability("WindowBlind")) { deviceInfo.put("type", 15) }
		//Check for Audio Volume - States for Mute are: ENUM ["unmuted", "muted"]
        if ( device.hasCapability("AudioVolume") ) { deviceInfo.put("type", 16) }
		myDeviceList << deviceInfo
    }
		
	//Now go through each of the sensor lists and get the Name, deviceID and type and put it into the deviceInfo
	def mySensorMap = [ myContacts: 31, myTemps: 32, myLeaks: 33, myMotion: 34, myPresence: 35, mySmoke: 36, myCarbonMonoxide: 37 ]

	mySensorMap.each { sensorKey, type ->
    	def deviceList = this."$sensorKey" // Dynamically get the list by its name
    	deviceList.each { device ->
        	def deviceInfo = new LinkedHashMap()
        	def deviceID = device.getId()
        	deviceInfo.put("ID", deviceID)
			deviceInfo.put("name", getShortName(device.displayName))
        	deviceInfo.put("type", type)
        	myDeviceList << deviceInfo
    	}
	}
		
	// Add device data to the list
    state.deviceList = myDeviceList
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

// Allows the client to send the changed Device List and JSON to the Hub.
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
    def group1 = slurper.parseText(state.JSON)  // This is the state of all of the devices as currerntly known on the Hub
    def group2 = slurper.parseText(bodyJson)   // This is the state of all of the devices as known to the App. It may alternately contain the customSortOrder.
	
    if (isLogDebug) log.debug ("Hub data (state.JSON): $group1")
    if (isLogDebug) log.debug ("App data: $group2")
	
	// Test to see whether we are receiving a customSortOrder. If so, we save it in state.customSortOrder as a JSON string.
	if (group2 instanceof Map && group2.customSortOrder instanceof List) {
		if (isLogDebug) log.debug(dodgerBlue("Received Updated Sort Order"))
        if (isLogDebug) log.debug(dodgerBlue("Existing Sort Order is: $state.customSortOrder"))
		def myCustomSortOrder = group2.customSortOrder

		// Convert the updated object back to a JSON string for storage into the state variable.
		state.customSortOrder = JsonOutput.toJson(myCustomSortOrder)
		if (isLogDebug) log.debug(dodgerBlue("New Sort Order is: $state.customSortOrder"))
		return // Nothing further to do.
	}
	
	//If the app is reporting a device state change if will now be processed
    // Map the second group by 'ID' for easier comparison
	def group2Map = group2.findAll { it instanceof Map && it.ID != null } .collectEntries { [(it.ID): it] }
    
    // Find changes
    def changes = []

    // Compare devices using ID as the constant
    group1.each { item1 ->
        def item2 = group2Map[item1.ID]
        if (item2) {  // If a matching ID is found
            def diff = [:]
            
            // Compare each key except for the ID and type (which are constants)
            item1.each { key, value ->
                // Only compare if the key exists in both group1 and group2
                if (item2.containsKey(key) && key != "ID" && key != "type") {
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
                changes << [ID: item1.ID, type: item1.type, changes: diff]  // Include ID and changes
            }
        }
    }
    
    log.info ("Changes are: $changes")

	// Print changes for each device one at a time
	changes.each { change ->
        def myDeviceName = findDeviceById(change.ID)
    	def key = change.changes.keySet().first()  // Get the key of the change (since there's only one)
    	def values = change.changes[key]           // Get the old and new values
    	if (isLogActions) log.info dodgerBlue("<b>Action: ${myDeviceName} (ID: ${change.ID}): $key: ${values[0]} ---> ${values[1]} </b>")
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
	if (sessionID != null && !state.updatedSessionList.contains(sessionID)) {
		state.updatedSessionList << sessionID
	}
	
	if (isLogDeviceInfo) log.info("<b>Downloading device data via fromHub():</b> $state.JSON")
	result = render contentType: "application/json", data: state.JSON, status: 200
	return result
}

//Receives a poll request from a session
def poll() {
    def sessionID = params.sessionID
	def result
	
    if (isLogTrace) log.trace("<b>Entering poll():</b> Session ID: $sessionID")
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
	def buttonNumber
	def buttonMap = ['Controls':1, 'Sensors':2, 'RenameDevices':3, 'Inactivity':4, 'Endpoints':5, 'Polling':6, 'Variables':7, 'CustomRows':8, 'Publish':9, 'Logging':10, 'General': 21, 'Appearance': 22, 'Title': 23, 'Columns':  24, 'Padding': 25, 'Advanced': 26, 'Experimental': 27]
	
	try {
		buttonNumber = buttonMap[btn]
		//if ( buttonNumber != null ) log.info ("buttonNumber is: $buttonNumber")
	}
	catch (Exception ignored) { }
	
	if (buttonNumber in 1..10)      { state.activeButtonA = buttonNumber; return }
	if (buttonNumber in 20..30)     { state.activeButtonB = buttonNumber; return }
	    
	switch (btn) {
		case 'EnableDragDrop':
			app.updateSetting("isDragDrop", true)
			compile()
			break
		case 'saveCustomSort':
			app.updateSetting("isDragDrop", false)
			compile()
			break
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
		case "applyTheme":
			applyTheme()
			break
        case "btnHideConfigure":
            state.hidden.Configure = state.hidden.Configure ? false : true
            break
        case "btnHideDesign":
            state.hidden.Design = state.hidden.Design ? false : true
            break
		case "btnHidePreview":
            state.hidden.Preview = state.hidden.Preview ? false : true
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
	if (section == "Configure") { if (state.hidden.Configure == true) return sectionTitle("Configure ▶") else return sectionTitle("Configure ▼") }
	if (section == "Preview") { if (state.hidden.Preview == true) return sectionTitle("Preview ▶") else return sectionTitle("Preview ▼") }
	if (section == "Design") { if (state.hidden.Design == true) return sectionTitle("Design ▶") else return sectionTitle("Design ▼") }
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
	def attributesToSubscribe = ["switch", "hue", "saturation", "level", "colorTemperature","valve","lock","speed","door","windowShade","position", "tilt", "mute","volume","contact","water","motion","presence","smoke","carbonMonoxide"]
	deleteSubscription()
	
	// Configure subscriptions to devices
	myDevices?.each { device ->
		attributesToSubscribe.each { attribute ->
			if (device.hasAttribute(attribute)) { subscribe(device, attribute, handler)	} 
		}
	}
	
	// Configure subscriptions to contacts
	myContacts?.each { device ->
		attributesToSubscribe.each { attribute ->
			if (device.hasAttribute(attribute)) { subscribe(device, attribute, handler)	} 
		}
	}
	
	// Configure subscriptions to temperatures
	myTemps?.each { device ->
		attributesToSubscribe.each { attribute ->
			if (device.hasAttribute(attribute)) { subscribe(device, attribute, handler)	} 
		}
	}
	
	// Configure subscriptions to leaks
	myLeaks?.each { device ->
		attributesToSubscribe.each { attribute ->
			if (device.hasAttribute(attribute)) { subscribe(device, attribute, handler)	} 
		}
	}
    
    // Configure subscriptions to motion
	myMotion?.each { device ->
		attributesToSubscribe.each { attribute ->
			if (device.hasAttribute(attribute)) { subscribe(device, attribute, handler)	} 
		}
	}
    
    // Configure subscriptions to Presence
	myPresence?.each { device ->
		attributesToSubscribe.each { attribute ->
			if (device.hasAttribute(attribute)) { subscribe(device, attribute, handler)	} 
		}
	}
    
    // Configure subscriptions to Smoke
	mySmoke?.each { device ->
		attributesToSubscribe.each { attribute ->
			if (device.hasAttribute(attribute)) { subscribe(device, attribute, handler)	} 
		}
	}
    
    // Configure subscriptions to Smoke
	myCarbonMonoxide?.each { device ->
		attributesToSubscribe.each { attribute ->
			if (device.hasAttribute(attribute)) { subscribe(device, attribute, handler)	} 
		}
	}
	
	// Configure subscriptions to variables
	for (int i = 1; i <= myVariableCount.toInteger(); i++) {
		if (settings["variableSource${i}"] == "Device Attribute") {
			if ( settings["myDevice${i}"]?.hasAttribute(settings["myAttribute${i}"]) ) {
				subscribe(settings["myDevice${i}"], settings["myAttribute${i}"], handler)	} 
				//if (isLogDebug) log.debug("Attribute Subscribed")
		}	
		
		if (settings["variableSource${i}"] == "Hub Variable") {
			if (isLogDebug) log.debug ("It's a Hub variable")
			variable = settings["myHubVariable${i}"].toString()
			subscribe(location, "variable:$variable", "handler")
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
    
	def tileLink1 = "[!--Generated:" + now() + "--][div style='height:100%; width:100%; scrolling:no; overflow:hidden;'][iframe name=" + state.AppID + "-A src=" + state.localEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe][/div]"
	def tileLink2 = "[!--Generated:" + now() + "--][div style='height:100%; width:100%; scrolling:no; overflow:hidden;'][iframe name=" + state.AppID + "-A src=" + state.cloudEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe][/div]"
	def tileLink3 = "[!--Generated:" + now() + "--][div style='height:100%; width:100%; scrolling:no; overflow:hidden;'][iframe name=" + state.AppID + "-B src=" + state.localEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe][/div]"
	def tileLink4 = "[!--Generated:" + now() + "--][div style='height:100%; width:100%; scrolling:no; overflow:hidden;'][iframe name=" + state.AppID + "-B src=" + state.cloudEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe][/div]"
	    
	if (settings.myRemote.toInteger() <= 20) { 
        if (isLogPublish) log.debug ("Running createTile")
        myStorageDevice.createTile(settings.myRemote, tileLink1, tileLink2, settings.myRemoteName) 
    }
    else {
        if (isLogPublish) log.debug ("Running createTile2")
        myStorageDevice.createTile2(settings.myRemote, tileLink1, tileLink2, tileLink3, tileLink4, settings.myRemoteName)
    }
			
	if (isLogPublish) log.info ("publishRemote: tileLink1 is: $tileLink1")
}

//This should get executed whenever any of the subscribed devices receive an update to the monitored attribute. Delays will occur if the eventTimeout is > 0
def handler(evt) {
	if (isLogTrace) log.trace("<b>Entering: handler with $evt</b>")
	
    //Handles the initialization of new variables added in code updates.
    initialize()
	
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
static String green(s) { return '<font color = "Green">' + s + '</font>' }

// Convert HSV to RGB using the values in the received map.
def getHEXfromHSV(hsvMap){
	//log.info ("getHEXfromHSV: hsvMap is: $hsvMap")
    def myRGB = hubitat.helper.ColorUtils.hsvToRGB([hsvMap.hue, hsvMap.saturation, hsvMap.value])
    def HEX = hubitat.helper.ColorUtils.rgbToHEX(myRGB)
    //log.info ("New HEX Color is: ${HEX}")
    return HEX
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

//Set the notes to a consistent style.
static String summary(myTitle, myText) {
    myTitle = dodgerBlue(myTitle)
    return "<details><summary>" + myTitle + "</summary>" + myText + "</details>"
}

//Used to create on screen buttons with links.
String buttonLink(String btnName, String linkText, int buttonNumber) {
	//if (isLogTrace) log.trace("<b>buttonLink: Entering with $btnName  $linkText  $buttonNumber</b>")
    def myColor = "#000000"
    def myText = "<b>${linkText}</b>"
    def myFont = 16

    if ((buttonNumber in 1..10 && buttonNumber == state.activeButtonA) ||
        (buttonNumber in 20..30 && buttonNumber == state.activeButtonB)) {
        myColor = "#ffFFFF"
        myText = "<b><u>${linkText}</u></b>"
    }
    return "<div class='form-group'><input type='hidden' name='${btnName}.type' value='button'></div><div><div class='submitOnChange' onclick='buttonClick(this)' style='color:${myColor};cursor:pointer;font-size:${myFont}px'>${myText}</div></div><input type='hidden' name='settings[$btnName]' value=''>"
}

//Get a list of supported attributes for a given device and return a sorted list.
def getAttributeList (thisDevice){
    if (thisDevice != null) {
        myAttributesList = []
        supportedAttributes = thisDevice.supportedAttributes
        supportedAttributes.each { attributeName -> myAttributesList << attributeName.name }
        return myAttributesList.unique().sort()
     }     
}

//Convert <HTML> tags to (HTML) for storage.
def unHTML(HTML){
    myHTML = HTML.replace("<", "[")
    myHTML = myHTML.replace(">", "]")
    return myHTML
}

//Convert (HTML) tags to <HTML> for display.
def toHTML(HTML){    
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
//**************  Remote Control APP-let Code
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
			--blinds : repeating-linear-gradient(to right, black 0%, #ccc 3%, black 3%, #ccc 6%, black 6%, #ccc 9%);
			--shades : linear-gradient( 3deg, #000 0%, #333 45%, #CCC 55%, #FFF 100%);
			--dimmer : linear-gradient(to right, #000 0%, #333 15%, #666 30%, #888 45%, #AAA 60%, #DDD 75%, #FFF 100% ); 
			--CT : linear-gradient(to right, #FF4500 0%, #FFA500 16%, #FFD700 33%, #FFFACD 49%, #FFFFE0 60%, #F5F5F5 66%, #FFF 80%,	#ADD8E6 100% ); 
			}  

	html { display: flex; flex-direction: column; align-items: #ha#; height: 100%; margin: 0px; margin-top:#tmt#px; font-family: 'Arial', 'Helvetica', sans-serif; box-sizing: border-box; border:0px solid red;}
	body { display: flex; flex-direction: column; align-items: center; flex-grow: 1; overflow: hidden; height: 100%; cursor: auto; box-sizing: border-box; margin:0px;}
	mark { #markTag# }
	m1 { #m1Tag# }
	m2 { #m2Tag# }
	m3 { #m3Tag# }
	m4 { #m4Tag# }
	m5 { #m5Tag# }
	.container {max-width: #maxWidth#px; align-items: center; width:100%; height: 100%; margin: 0px; overflow:auto; box-sizing: border-box; padding: #tpad#px;}
	
	/* Mobile Screens  */
	@media (min-resolution: 150dpi) and (hover:none) and (pointer: coarse) {
		.container {max-width:95%; width:100%; margin:#tmt#px auto; outline: 0px dotted red;} 
		html, body {justify-content: flex-start; align-items: stretch; height:auto; overflow:auto; } 
	}

	/* Eliminates scrollbars within the dashboard for Webkit browsers. For Firefox scrollbar-width: none; eliminates scrollbars within the Dashboard */
	//::-webkit-scrollbar {display: none;}

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
	table {width: calc(100%); border-collapse: collapse; table-layout: auto; border: #bwo#px solid #bc#; margin: 0 auto;}
	th, td { padding: #tvp#px #thp#px; text-align:center; vertical-align:middle; border: #bwi#px solid #bc#; transition:background-color 0.3s; user-select:none;}
	th { background-color: #hbc#; font-weight: bold; font-size: #hts#%; color: #htc#; margin:1px; }
	
	.ascSort { border-bottom: #bwi#px solid #sortHeaderHintAZ#;}
	.descSort { border-bottom: #bwi#px solid #sortHeaderHintZA#;}

	tr { background-color: #rbc#; color: #rtc#; font-size:#rts#%;}
	tr:hover { background-color: #rbs#;} 
    
	.selected-row {	background-color: #rbs#;}
	.custom-row-separator {background-color: #crbc#; color:#crtc#; font-size: #crts#%;}
	//Drag and Drop Support
	#isDragDropCSS#

	/* Widths of columns 1, 2 & 10 are derived from the width of the control element. Columns 5 and 6 are fixed as set by the user. The remaining columns with be auto-calculated to best fit the text. */
	th:nth-child(1), td:nth-child(1) { width:calc(var(--control) * 1.5); display:#hideColumn1#; }
	th:nth-child(2), td:nth-child(2) { width:calc(var(--control) * 2.5); display:#hideColumn2#; }
	th:nth-child(3) {padding-left:calc(#thp#px + 5px);}
	td:nth-child(3) {text-align:left; padding-left:calc(#thp#px); }
	th:nth-child(4), td:nth-child(4) { width:calc(var(--control) * 3); display:#hideColumn4#; }
	th:nth-child(5), td:nth-child(5) { width:#column5Width#px; display:#hideColumn5#; }
	th:nth-child(6), td:nth-child(6) { width:#column6Width#px; display:#hideColumn6#; }
	th:nth-child(7), td:nth-child(7) { display:#hideColumn7#; }
	th:nth-child(8), td:nth-child(8) { display:#hideColumn8#; }
	th:nth-child(9), td:nth-child(9) { display:#hideColumn9#; }
	th:nth-child(10), td:nth-child(10) { width:calc(var(--control) * 2); display:#hideColumn10#; }
	th:nth-child(11), td:nth-child(11) { display:#hideColumn11#; }
	th:nth-child(12), td:nth-child(12) { display:#hideColumn12#; }
	
	/* START OF CONTROLS CLASSES */			
	/* Column 1 Checkboxes */
	input[type="checkbox"] {height:var(--control); width:var(--control); margin:0px; margin-top:3px; cursor: pointer; }

	/* Column 2 - Materials Symbols - Icons */
	.material-symbols-outlined {padding:3px; border-radius: 50%; font-size:calc(var(--control) * 1.5 )}
	.material-symbols-outlined.on { background-color:rgba(255,255,0, 0.3); color: #333333;}
	.material-symbols-outlined.off {color: #000000; opacity:0.5;}
	.material-symbols-outlined.group {color: #000000; opacity:0.8;}
	.open { background-color:rgba(255,213,128, 0.7); color:#333333;}
	.good { background-color:rgba(0,255,0, 0.7); color:#333333;}
	.warn { background-color:rgba(255,140,0, 0.7); color:#333333;}
	.bad { background-color:rgba(255,0,0, 0.7); color:#333333;}
			
	/* Column 4 On/Off Switch */
	.toggle-switch { position: relative; display: inline-block; vertical-align: middle; margin-top: calc(var(--control) / 3); margin-bottom: calc(var(--control) / 5 ); width: calc(var(--control) * 2); height: var(--control); background-color: #CCC; cursor: pointer; border-radius: calc(var(--control) / 2); transition: background-color 0.3s ease; box-shadow: 0 0 calc(var(--control) / 1.5) 0px rgba(255, 99, 71, 1); }
	.toggle-switch::before { content: ''; position: absolute; width: calc(var(--control) * 0.87); height: calc(var(--control) * 0.87); border-radius: 50%; background-color: white; top: calc(var(--control) * 0.066); left: calc(var(--control) * 0.066); transition: transform 0.3s ease; }
	.toggle-switch.on { background-color: #2196F3; box-shadow: 0 0 calc(var(--control) / 1.5) calc(var(--control) / 5) rgba(255, 255, 0, 1); }
	.toggle-switch.on::before { transform: translateX(calc(var(--control))); }

	/* Column 5 - Control Group 1 - Level and Kelvin Sliders */
	.control-container {display:flex;position: relative; width: 95%; display: flex; justify-content: center; align-items: center; background-color:#rbc#; margin:auto; }
	.CT-slider, .level-slider, .blinds-slider, .shades-slider, .volume-slider, .tilt-slider { width: 90%; opacity:0.75; border-radius:0px; height:var(--control); outline: 2px solid #888; cursor: pointer;}
	.CT-value, .level-value, .blinds-value, .shades-value, .volume-value, .tilt-value, .state-value {position:absolute; top:50%; transform:translateY(-50%); font-size:#rts#%; pointer-events:none; text-align:center; cursor:pointer; font-weight:bold; background:#fff8; padding:0px;color: #rtc#;}
	.state-text {font-size:#rts#%; pointer-events:none; cursor:pointer;}

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
	.CT-slider::-moz-range-track { background: var(--CT); height: 100%; }
	.level-slider::-moz-range-track { background: var(--dimmer); height: 100%; }   
	.blinds-slider::-moz-range-track { background: var(--blinds); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.shades-slider::-moz-range-track { background: var(--shades); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.volume-slider::-moz-range-track { background: var(--tickMarks), linear-gradient(to right, green, orange, red); height: 100%; background-size: 100% 100%; background-repeat: no-repeat; border: 0px solid #DDD; }
	.tilt-slider::-moz-range-track { background: var(--tickMarks), linear-gradient(to right, #000 0%, #333 10%, #0D47A1 30%, #17D 40%, #4682B4 50%, #8CF 65%, #FFF 85%, #FFF 100%); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.level-slider::-moz-range-thumb, .CT-slider::-moz-range-thumb, .shades-slider::-moz-range-thumb, .blinds-slider::-moz-range-thumb, .volume-slider::-moz-range-thumb { background: dodgerblue; border-radius: 50%; cursor: pointer;}
	
	/* Column 6 - Control Group 2 - Color */
	input[type="color"]::-webkit-color-swatch-wrapper { padding: 0px; }
	.colorPicker{border: 2px solid #888; border-radius: 2px; width: calc(var(--control) * 3); height: calc( var(--control) * 1.25); cursor: pointer;}	/* Column 6 Color Control */

	/* Info Columns 7 & 8 & 9 */
	.info1, .info2, .info3 { border:none; background:transparent; white-space:nowrap; color:#rtc#;}
	.info1 { font-size: #its1#%; text-align: #ita1#; }
	.info2 { font-size: #its2#%; text-align: #ita2#; }
	.info3 { font-size: #its3#%; text-align: #ita3#; }

	/* Define glow effects */	
	.glow-EffectSuccess {border-color: #pollUpdateColorSuccess#;}
	.glow-EffectFail {border-color: #pollUpdateColorFail#;}
	.glow-EffectPending {border-color: #pollUpdateColorPending#;}
	.glow-EffectCT {outline: 2px solid #1E90FF;}
	.glow-EffectRGB {border: 2px solid #1E90FF;}
		
	/* Refresh bar styling */
	#shuttle { display:none; position:relative; height:#shuttleHeight#px; width:5%; background-color:#shuttleColor#; border-radius:3px; animation:none; top: -#shuttleHeight#px;}
	@keyframes slide {0% { transform: translateX(0%); } 50%  { transform: translateX(1900%); } 100% { transform: translateX(0%); } }
	@keyframes blink { 0% { opacity: 1; } 50% {opacity: 0;} 100% {opacity: 1;} }
	.blinking { animation: blink 1s infinite; }
	.blinking_orange { animation: blink 1s infinite; background-color: rgba(255, 140, 0, 0.7); }

	.button-group { display: flex; justify-content: space-between; align-items: center; margin: 0 auto; text-align: center;}
	.button { flex:1;  max-height: var(--control); padding: 4px 8px; margin: 0 2px; border-radius: var(--control); background-color: #555555; text-align: center; transition: background-color 0.3s, border-color 0.3s, outline 0.3s; 
			display: flex; align-items: center; justify-content: center; color: #FFF; font-size: calc(2 + var(--control) / 1.5); cursor: pointer; }
	.button:hover { background-color: #3CB371;}
	.button:active { background-color: #1C86EE;}
	.button.selected { background-color: #1E90FF;}
	.button-group.disabled { opacity: 0.75; pointer-events: none; cursor: not-allowed; }

	@keyframes spin { from {transform: rotate(0deg);} to {transform: rotate(360deg);} }
	.spin-low {animation: spin 3s linear infinite;}
	.spin-medium {animation: spin 1.5s linear infinite;}
	.spin-high {animation: spin 0.75s linear infinite;}

    .modal {display:none; position:fixed; z-index:1000; left:0; top:0; width:100%;height:100%; background-color:rgba(0,0,0,0.5);overflow: auto;}
	.modal-content {background-color:white; margin:20px auto; padding:20px; border-radius:8px; width:300px; text-align:left;overflow-y: auto;}
	.modal-content select, .modal-content input { margin-bottom: 10px; font-size: 16px; }
    .close {cursor:pointer; float:right; font-size: 24px}
	.group-icon-active { transform: rotate(0deg); transition: transform 0.3s ease;}
	.group-icon-inactive { transform: rotate(90deg); background-color: #F0F0F0; border-radius: 50%; padding: 2px; transition: transform 0.5s ease, background-color 0.5s ease; }

	iframe {position: relative; z-index: 10; pointer-events: auto;}
}

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
				<th><input type="checkbox" id="masterCheckbox" onclick="toggleAllCheckboxes(this)" title="Select All/Deselect All"></th>
				<th id="icon" class="sortLinks" onclick="sortTable(1, this);" title="Icon - Sort"><span id="iconHeader">Icon</span></th>
				<th id="nameHeader" class="sortLinks" onclick="sortTable(2, this);"> <div style="display: flex; justify-content: space-between; align-items: center;">
	        		<span title="Sort Name A-Z">#column3Header#</span><span id="refreshIcon" style="font-size: 1.5em; cursor:pointer;" onclick="event.stopPropagation(); refreshPage(50);" 
					title="Refresh Data"><b>↻</b></span></div></th>
				<th id="stateHeader" class="sortLinks th" onclick="sortTable(3, this);"><span title="Sort State On-Off">State</span></th>
				<th id="ControlAB" class="sortLinks" onclick="toggleControl()" title="Toggle Control A/B">#column5Header#</th>
				<th id="color">#column6Header#</th> 
				<th id="i1" class="sortLinks" onclick="sortTable(6, this);" title="Info1 - Alpha Sort"><span id="info1Header">#Info1#</span></th>
				<th id="i2" class="sortLinks" onclick="sortTable(7, this);" title="Info2 - Alpha Sort"><span id="info2Header">#Info2#</span></th>
				<th id="i3" class="sortLinks" onclick="sortTable(8, this);" title="Info3 - Alpha Sort"><span id="info3Header">#Info3#</span></th>
				<th id="Unused" title="Unused"><span id="unusedHeader">Unused</span></th>
				<th id="sort">Custom Sort</th>
				<th id="group">Group</th>
			</tr>
		</thead>
		<tbody><!-- Table rows will be dynamically loaded from JSON --></tbody>
	</table>
	<div id="shuttle"></div>
</div>

<!-- Settings Window -->
<div id="myModal" class="modal">
    <div class="modal-content">
        <h2 style="text-align: center;"><u>Settings</u></h2>
        <label for="filterSwitch"><strong>Display Switches:</strong></label>
        <select id="filterSwitch"><option value="allSwitch" selected>All Switches</option><option value="onlyOn">Only On</option></select><br>
		<label for="filterContact"><strong>Display Contacts:</strong></label>
        <select id="filterContact"><option value="allContact" selected>All Contacts</option><option value="onlyOpen">Only Open</option></select><br>
		<label for="filterLeak"><strong>Display Leak Sensors:</strong></label>
        <select id="filterLeak"><option value="allLeak" selected>All Sensors</option><option value="onlyWet">Only Wet</option></select><br>
		<label for="filterLock"><strong>Display Locks:</strong></label>
        <select id="filterLock"><option value="allLock" selected>All Locks</option><option value="onlyUnlocked">Only Unlocked</option></select><br>
		<label for="filterMotion"><strong>Display Motion:</strong></label>
        <select id="filterMotion"><option value="allMotion" selected>All Motion</option><option value="onlyActive">Only Active</option></select><br>
        <label for="isPolling"><strong>Polling Enabled:</strong></label>
		<select id="isPolling"><option value="true">true</option><option value="false">false</option></select><br>
		<label for="pollInterval"><strong>Poll Interval(ms):</strong></label>
		<input type="number" id="pollInterval" value="5000" min="1000" max="60000" step="1000" /><br>
		<label for="isLogging"><strong>Logging Enabled:</strong></label>
		<select id="isLogging"><option value="true">true</option><option value="false">false</option></select><br><br>
		<button id="modalCloseBtn" style="display: block; margin: 0 auto; width: 50%; padding: 10px; font-size: 1rem; text-align: center;"><b>Close</b></button>
    </div>
</div>

<script>
// Global variables to track resources that need cleanup
let pollingInterval;
let pressTimer;					// Used to determine when to pop up the modal screen
let currentFetchController;		//Used for the fetch operation
let pollingTimeoutID = null;	//Handle for the Polling Timeout
const separatorRow = 51;
const deviceRow = 52;
const iFrameRow = 53;
const valve = 10;
const stateMap = #deviceStateMap#;

// Ensure each iframe has a unique window.name to scope storage keys
// The iFrame preview in the composer has a window.name of "AppID" followed by a "-P" (Preview) so it looks like "4912-P".  An Applet loaded elsewhere will have have either a "-A" or "-B" suffix depending on which link is being used.
if (!window.name) { window.name = 'applet_' + Math.random().toString(36).substr(2, 5); }

// Generate a scoped key using window.name instead of AppID
const storageKey = (key) => `${window.name}_${key}`;

// Modal constants
const modal = document.getElementById("myModal");

// LocalStorage operations (persistent per iframe)
let storedSortDirection = JSON.parse(localStorage.getItem(storageKey("sortDirection")));
let sortDirection = storedSortDirection || { activeColumn: 2, direction: 'asc' };
let showSlider = ( localStorage.getItem(storageKey("showSlider")) === "A" || localStorage.getItem(storageKey("showSlider")) === "B" ) ? localStorage.getItem(storageKey("showSlider")) : "A";
let lastCommand = "none";
let isSliding = false;
let pollingActive = true;
let isWindowActive = true;

// Use localStorage (instead of sessionStorage) for all settings
let sessionID = localStorage.getItem(storageKey("sessionID"));
if (!sessionID) { 
	sessionID = (Math.abs(Math.random() * 0x7FFFFFFF | 0)).toString(16).padStart(8, '0');
	localStorage.setItem(storageKey("sessionID"), sessionID);
	}

if (!localStorage.getItem(storageKey("Collapsed_Groups"))) { localStorage.setItem(storageKey("Collapsed_Groups"), JSON.stringify([])); }
const COLLAPSED_GROUPS_KEY = storageKey("Collapsed_Groups");

let pollInterval = localStorage.getItem(storageKey("pollInterval")) || "#pollInterval#";
localStorage.setItem(storageKey("pollInterval"), pollInterval);

// Initialize isPolling as a Boolean
if (!localStorage.getItem(storageKey("isPolling"))) { localStorage.setItem(storageKey("isPolling"), "#isPolling#"); }
let isPolling = localStorage.getItem(storageKey("isPolling")) === "true";

// Initialize Logging as a Boolean
if (!localStorage.getItem(storageKey("isLogging"))) { localStorage.setItem(storageKey("isLogging"), "false"); }
let isLogging = localStorage.getItem(storageKey("isLogging")) === "true";

let isDragDrop = #isDragDrop#;
let isCustomSort = #isCustomSort#;
const shuttle = document.getElementById('shuttle');

//Disable Polling if we are using Drag and Drop
if ( isDragDrop ) isPolling = false;

//These are all the filterValues used in the Modal window
const filterValues = {};
["filterSwitch", "filterContact", "filterLeak", "filterLock", "filterMotion"].forEach(id => {
  const key = storageKey(id);
  let value = localStorage.getItem(key);
  if (!value) {
    const defaultVal=id === "filterSwitch" ? "allSwitch" : id === "filterContact" ? "allContact" : id === "filterLeak" ? "allLeak" : id === "filterLock" ? "allLock" : id ==="filterMotion" ? "allMotion" : "";
    localStorage.setItem(key, defaultVal);
    value = defaultVal;
  }
  document.getElementById(id).value = value;
  filterValues[id] = value;   // Store current values if needed later
  document.getElementById(id).addEventListener("change", function () {
    localStorage.setItem(key, this.value);
  });
});

document.getElementById("isPolling").addEventListener("change", function() { localStorage.setItem(storageKey("isPolling"), this.value);});
document.getElementById("pollInterval").addEventListener("change", function() { localStorage.setItem(storageKey("pollInterval"), this.value);});
document.getElementById("isLogging").addEventListener("change", function() { localStorage.setItem(storageKey("isLogging"), this.value);});

//Output some logging Information if it is enabled
console.log ("isLogging", isLogging);
if (isLogging) console.log("window.name:", window.name);
if (isLogging) console.log("storageKey:", storageKey);
if (isLogging) console.log ("isDragDrop", isDragDrop);
if (isLogging) console.log ("isPolling", isPolling);
if (isLogging === 'true'){ showVariables(); }

//Polling Control
if ( isPolling ) { 
	const poller = startPolling('#URL1#', pollResult); 
	//Start the Shuttle animation
	const shuttle = document.getElementById('shuttle');
	shuttle.style.display = 'block';
	shuttle.style.animation = `slide ${pollInterval *2}ms ease-in-out infinite`;
}

//This has to do with transaction handling
let transactionTimeout = #commandTimeout#;
let transaction = null;

// Setup event listeners for the name header with proper cleanup
const nameHeader=document.getElementById("nameHeader");
function setupHeaderEventListeners() {
  // Remove existing listeners first to prevent duplicates
  removeHeaderEventListeners();
  
  // Add new listeners
  nameHeader.addEventListener("mousedown", startPress, { passive:true });
  nameHeader.addEventListener("touchstart", startPress, { passive:true });
  ["mouseup", "touchend", "dragstart", "dragend"].forEach(e => nameHeader.addEventListener(e, cancelPress));
}

function removeHeaderEventListeners() {
  nameHeader.removeEventListener("mousedown", startPress);
  nameHeader.removeEventListener("touchstart", startPress);
  ["mouseup", "touchend", "dragstart", "dragend"].forEach(e => nameHeader.removeEventListener(e, cancelPress));
}

// Initialize event listeners
setupHeaderEventListeners();

// Modal control functions
function openModal() { modal.style.display = "block"; document.getElementById("isPolling").value = isPolling; document.getElementById("isLogging").value = isLogging; document.getElementById("pollInterval").value = pollInterval;}
function closeModal() { modal.style.display = "none"; refreshPage(50);}
function startPress(event) { if (pressTimer) clearTimeout(pressTimer); pressTimer=setTimeout(openModal, 1999); }
function cancelPress() { if (pressTimer) { clearTimeout(pressTimer); pressTimer = null; } }
document.getElementById("modalCloseBtn").addEventListener("click", closeModal);


//***********************************************  Table Body  *************************************************************************
//**************************************************************************************************************************************

function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function showVariables(){
	console.log("Session Storage Variables:");
	for (let i = 0; i < sessionStorage.length; i++) {
	    let key = sessionStorage.key(i);
	    console.log(`${key}: ${sessionStorage.getItem(key)}`);
	}
}

function loadTableFromJSON(data) {
	const fragment = document.createDocumentFragment();
	const tbody = document.querySelector("#sortableTable tbody");
	let icon = "";
	tbody.innerHTML = "";
	const saved = JSON.parse(sessionStorage.getItem(storageKey("checkboxStates"))) || {};
	
	data.forEach((d, i) => {
		let show = true;
		if (filterValues["filterSwitch"] === "onlyOn" && d.type >= 1 && d.type <= 5 && d.switch !== "on") show = false;
		if (filterValues["filterLock"] === "onlyUnlocked" && d.type === 11 && d.switch !== "off") show = false;
		if (filterValues["filterContact"] === "onlyOpen" && d.type === 31 && d.switch !== "open") show = false;
		if (filterValues["filterLeak"] === "onlyWet" && d.type === 33 && d.switch !== "wet") show = false;
		if (filterValues["filterMotion"] === "onlyActive" && d.type === 34 && d.switch !== "active") show = false;
		if (!show) return;

		const row = document.createElement('tr');
		row.draggable = #isDragDrop#;
		Object.assign(row.dataset, {
			ID: d.ID, name: d.name, type: d.type, speed: d.speed, level: d.level,
			position: d.position, tilt: d.tilt, volume: d.volume, colorMode: d.colorMode || "None",
			info1: d.i1, info2: d.i2, info3: d.i3, icon: d.icon, class: d.cl, row: d.row, group: d.group
		});
		
		const col = /^#[0-9A-F]{6}$/i.test(d.color) ? d.color : "#FFF";

		//Show or Hide Groups when using Custom Sort.
		if (d.type === separatorRow) icon = `<i class='material-symbols-outlined ${d.cl}' onclick="toggleGroupVisibility(this)">${d.icon}</i>`;
		else icon = `<i class='material-symbols-outlined ${d.cl}'>${d.icon}</i>`;
				
		const getSlider = (cls, val, min, max, label, disp, extra = "") => `
			<input type="range" class="${cls}" min="${min}" max="${max}" value="${val}"
				style="display:${disp}" oninput="updateSliderValue(this, '${label}')" onchange="updateHUB()" ${extra}>
			<span class="${label}-value" style="display:${disp}">${val}${label === "CT" ? "°K" : "%"}</span>`;

		let c1 = "", c2 = "";
		if (d.type <= 5) {
			c1 += `<div class="control-container">`;
			if ([2,3,4,5].includes(d.type)) c1 += getSlider("level-slider", d.level, 0, 100, "level", showSlider === 'A' ? 'block' : 'none');
			if ([3,5].includes(d.type)) c1 += getSlider(`CT-slider ${d.colorMode === 'CT' ? 'glow-EffectCT' : ''}`, d.CT, 2000, 6500, "CT", showSlider === 'B' ? 'block' : 'none');
			c1 += `</div>`;
		}
		if (d.type === 12) c1 = `
			<div class="button-group ${d.switch === 'off' ? 'disabled' : ''}">
				${["low","medium","high"].map(s => `<div class="button ${d.speed === s ? 'selected' : ''} ${d.switch === 'off' ? 'disabled' : ''}" data-speed="${s}" onclick="speed(this); updateHUB(); toggleChecked(this)">${s[0].toUpperCase()}</div>`).join("")}
			</div>`;
		if ([14,15].includes(d.type)) c1 = `<div class="control-container"><input type="range" class="shades-slider" min="0" max="100" value="${d.position}" oninput="updateSliderValue(this, 'position')" onchange="updateHUB()"><span class="shades-value"><b>${d.position}%</b></span></div>`;
		if (d.type === 15) c2 = `<div style="display:flex;align-items:center;"><div class="control-container"><input type="range" class="tilt-slider" min="0" max="90" value="${d.tilt}" oninput="updateSliderValue(this,'tilt')" onchange="updateHUB()"><span class="tilt-value">${d.tilt}°</span></div><div id="tilt-indicator" class="tilt-indicator" style="margin-left:20px;margin-right:10px;">|</div></div>`;
		if (d.type === 16) c1 = `<div class="control-container"><input type="range" class="volume-slider" min="0" max="100" value="${d.volume}" oninput="updateSliderValue(this, 'volume')" onchange="updateHUB()"><span class="volume-value">${d.volume}%</span></div>`;
		if ([4,5].includes(d.type)) c2 = `<input type="color" class="colorPicker ${d.colorMode === 'RGB' ? 'glow-EffectRGB' : ''}" id="colorInput${i}" value="${col}" onchange="updateColor(this); updateHUB()">`;

		const state = d.type <= 30
			? `<div class="toggle-switch ${d.switch === 'on' ? 'on' : ''}" data-state="${d.switch}" onclick="toggleSwitch(this); updateHUB()"></div>`
			: `<div class="state-text">${d.switch}</div>`;

		//For Separator and Custom Rows put the text contents into the fields
		if (d.type === separatorRow || d.type === deviceRow) {c1 = d.level; c2 = d.CT};
		if ([1,10,11,13].includes(d.type)) c1 = "";

        if (d.type === 53) {
            // iFrame rows get a custom column configuration. It is sized to fill up all visible rows between column 3 (state) and the column 10 (unused)
            row.innerHTML = `<td colspan="1"></td><td colspan="1">${icon}</td><td colspan="#iFrameColspan#">${state}</td><td style="display:#hideColumn11#;">${d.row}</td><td style="display:#hideColumn12#;">${d.group}</td>`;
		} else {
            // Default: full multi-column row
            row.innerHTML = `
                <td><input type="checkbox" class="option-checkbox" ${saved[d.ID] ? "checked" : ""} onchange="toggleRowSelection(this)"></td>
                <td>${icon}</td><td>${d.name}</td><td>${state}</td><td>${c1}</td><td>${c2}</td>
                <td><div class="info1">${d.i1}</div></td><td><div class="info2">${d.i2}</div></td><td><div class="info3">${d.i3}</div></td>
                <td></td><td>${d.row}</td><td>${d.group}</td>`;
        }
		fragment.appendChild(row);
	});
  
	// Add all rows to the DOM at once
  	tbody.appendChild(fragment);

	if (isDragDrop) {
		tbody.querySelectorAll("tr").forEach(r => {
			r.addEventListener("dragstart", e => e.target.classList.add("dragging"));
			r.addEventListener("dragend", e => { e.target.classList.remove("dragging"); saveRowOrder(); });
		});
		tbody.addEventListener("dragover", e => {
			e.preventDefault();
			const d = document.querySelector(".dragging");
			const a = [...tbody.querySelectorAll("tr:not(.dragging)")].reduce((c, el) => {
				const o = e.clientY - el.getBoundingClientRect().top - el.offsetHeight / 2;
				return o < 0 && o > c.offset ? { offset: o, el } : c;
			}, { offset: -Infinity }).el;
			tbody.insertBefore(d, a ?? null);
		});
	}

	if (isCustomSort) { sortTable(10); setColumnHeaders(true); }
	else setColumnHeaders(false);

	//Sets the group number for each row
	if (isCustomSort) assignGroupNumbers();

	// Restore collapsed groups from localStorage
	restoreCollapsedGroups();
	
	updateAllTiltIndicators();
	//Tweak the contents of the Rows for last minute changes.
	updateRows();

	updateState();

}

//******************************  Custom Rows, Manual Sort Order and Collapsible Groups ************************************************
//**************************************************************************************************************************************

//Updates the value of the State field to display a different text, language or formatting.
function updateState() {
    document.querySelectorAll("#sortableTable tr").forEach(row => {
        const stateCell = row.cells[3]; // Adjust this index if necessary
        if (!stateCell) return;

        const rawState = stateCell.textContent.trim().toLowerCase();
        if (stateMap?.hasOwnProperty(rawState)) {
            stateCell.innerHTML = stateMap[rawState];  // ← This allows HTML formatting
        }
    });
}

//Formats the appearance of the Custom Rows. 51: Separator Row, 52: Device Row, 53: iFrame Row  
function updateRows() {
    document.querySelectorAll("#sortableTable tr").forEach(row => {
        if (Number(row.dataset.type) === separatorRow) {
            row.style.background = "linear-gradient(to bottom, #crbc#, #crbc2#)";
            [...row.cells].forEach((cell, i) => {
                if (!cell) return;
                if ([1, 2, 3].includes(i)) cell.classList.add(".custom-row-separator");
                if (i === 0) cell.innerHTML = "";
                cell.style.borderRight = "2px solid transparent";
            });
        }
		//Remove the Selection box for anything of type >= 30, namely sensors or custom rows.
		if (+row.dataset.type >= 30) {
            const cell = row.cells[0];
            if (cell) cell.innerHTML = "";
        }
    });
}

//Save the sort order after they are manually sorted.
function saveRowOrder() {
    const rows = [...document.querySelectorAll("#sortableTable tbody tr")];
    // Track how many times each ID has been seen
    const seen = {};
    const sortedJSON = rows.map((row, index) => {
        const baseID = row.dataset.ID;
        if (!seen[baseID]) seen[baseID] = 0;
        seen[baseID]++;
        // If it's a duplicate, append a suffix to ensure uniqueness
        const uniqueID = seen[baseID] > 1 ? `${baseID}__${seen[baseID]}` : baseID;
		const UID = row.dataset.ID + "-" + row.dataset.type
        return { row: index + 1, ID: uniqueID, UID: UID };
    });

    sessionStorage.setItem(storageKey("customSortOrder"), JSON.stringify(sortedJSON));
    const output = { customSortOrder: sortedJSON };
	const myData = JSON.stringify(output);
	//console.log(`customSortOrder is: ${myData}`);
    sendData(myData);
}

//Assigns a Group number to each row so we can Hide all members of the group with the same Group number.
function assignGroupNumbers() {
	const rows = document.querySelectorAll("#sortableTable tbody tr");
	let group = 0;
	rows.forEach(row => {
		const typeStr = row.dataset.type;
		const type = parseInt(typeStr);
		//console.log(`Row type: ${typeStr} parsed as ${type}, current group: ${group}`);
		if (type === separatorRow) {
			group++;
			console.log(`Separator found, incrementing group to ${group}`);
		}
		row.dataset.group = group;
		const groupCell = row.querySelector("td:last-child");
		if (groupCell) groupCell.textContent = group;
	});
}


//***********************************************  Collapsible Groups  *****************************************************************
//**************************************************************************************************************************************

//Add a collapsed group to the local settings
function addCollapsedGroup(groupNum) {
  const list = JSON.parse(localStorage.getItem(COLLAPSED_GROUPS_KEY) || "[]");
  if (!list.includes(groupNum)) {
    list.push(groupNum);
    localStorage.setItem(COLLAPSED_GROUPS_KEY, JSON.stringify(list));
  }
}

//Remove a collapsed group to the local settings
function removeCollapsedGroup(groupNum) {
	let list = JSON.parse(localStorage.getItem(COLLAPSED_GROUPS_KEY) || "[]");
	list = list.filter(n => n !== groupNum);
	localStorage.setItem(COLLAPSED_GROUPS_KEY, JSON.stringify(list));
}

function setGroupCollapsedState(groupNum, collapsed) {
    const rowsInGroup = [...document.querySelectorAll(`#sortableTable tr[data-group="${groupNum}"]`)];
    if (rowsInGroup.length === 0) return;

    // Find the separator row (the one with type === separatorRow)
    const iconRow = rowsInGroup.find(r => Number(r.dataset.type) === separatorRow);
    if (!iconRow) return;

    const icon = iconRow.querySelector("i.material-symbols-outlined");
    if (!icon) return;
	
	// Hide all rows except iconRow when collapsed; else show all
    rowsInGroup.forEach(row => { row.style.display = (collapsed && row !== iconRow) ? "none" : ""; });

    // Icon class toggle: active means expanded (not collapsed)
    icon.classList.toggle("group-icon-active", !collapsed);
    icon.classList.toggle("group-icon-inactive", collapsed);
}

//Collapses or Expands the Groups.
function toggleGroupVisibility(iconElement) {
    const row = iconElement.closest("tr");
    if (!row) return;

    const group = row.dataset.group;
    if (!group) return;

    const rowsInGroup = [...document.querySelectorAll(`#sortableTable tr[data-group="${group}"]`)];
    const otherRows = rowsInGroup.filter(r => r !== row);

    // Determine if currently collapsed by checking if all other rows are hidden
    const currentlyCollapsed = otherRows.every(r => r.style.display === "none");
    const newCollapsedState = !currentlyCollapsed;

    setGroupCollapsedState(Number(group), newCollapsedState);

    if (newCollapsedState) { addCollapsedGroup(Number(group));} 
    else { removeCollapsedGroup(Number(group)); } 
}

//Restores the settings for Collapsed Groups after a refresh
function restoreCollapsedGroups() {
    const collapsedGroups = JSON.parse(localStorage.getItem(COLLAPSED_GROUPS_KEY) || "[]");
    collapsedGroups.forEach(groupNum => setGroupCollapsedState(groupNum, true));
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

function updateHUB() {
	//Update the table border to show activity is pending if the lastCommand is something other than "none".
	document.querySelector("table").classList.add('glow-EffectPending');
	const output = [...document.querySelectorAll("#sortableTable tbody tr")].map(row => {
		const { type, ID } = row.dataset;
		const t = Number(type);
		if (t > 30) return null;	//Don't return anything if it is a sensor or custom device.

		const o = { ID, type };
		const $ = s => row.querySelector(s);
		const add = (cond, key, valFn) => cond && (o[key] = valFn());

		add(t >= 1 && lastCommand === "switch", "switch", () => $(".toggle-switch")?.dataset.state);
		add(t >= 2 && t <= 5 && lastCommand === "level", "level", () => +$(".level-slider")?.value || 0);
		add((t === 3 || t === 5) && lastCommand === "CT", "CT", () => +$(".CT-slider")?.value || 0);
		add((t === 4 || t === 5) && lastCommand === "color", "color", () => ($('input[type="color"]')?.value || "#000000").toUpperCase());
		add(t === 12 && lastCommand === "speed", "speed", () => $(".button.selected")?.dataset.speed || "off");
		add((t === 14 || t === 15) && lastCommand === "position", "position", () => +$(".shades-slider")?.value || 0);
		add(t === 15 && lastCommand === "tilt", "tilt", () => +$(".tilt-slider")?.value || 0);
		add(t === 16 && lastCommand === "volume", "volume", () => +$(".volume-slider")?.value || 0);
		return o;
	}).filter(Boolean);

	if (isLogging) console.log("Output is:", output);
	sendData(JSON.stringify(output));
}

//Sends the Data to the Hub
function sendData(payload) {
	console.log ("Sending Data");
	handleTransaction("begin");
    const url = '#URL#';
    fetch(`${url}&sessionID=${sessionID}`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: payload }).catch(error => {
        console.error('Error:', error);
    });
}

// Function to fetch JSON data from a URL and return it
async function fetchData() {
  if (isLogging) { console.log("fetchData(): Downloading data from Hub.") };
  const url = '#URL#'; 
  
  // Cancel any ongoing fetch
  if (currentFetchController) {
    currentFetchController.abort();
  }
  
  // Create new controller
  currentFetchController = new AbortController();
  const signal = currentFetchController.signal;
  
  try {
    const response = await fetch(`${url}&sessionID=${sessionID}`, { signal });
    const jsonData = await response.json();
    return jsonData;
  } catch (error) {
    if (error.name === 'AbortError') {
      console.log("Fetch aborted");
    } else {
      console.error("Error fetching data:", error);
    }
    return null;
  } finally {
    currentFetchController = null;
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
function updateSliderValue(slider, cmd) {
  const row = slider?.closest('tr'), cb = row?.querySelector("input[type='checkbox']");
  slider.nextElementSibling.innerText = slider.classList.contains('tilt-slider') ? `${slider.value}°` : `${slider.value}%`;
  if (cmd === "CT") row.dataset.colorMode = "CT";
  if (cmd === "tilt") updateAllTiltIndicators();
  if (cb?.checked) syncRows(cmd, slider.value);
  lastCommand = cmd;
}


// Toggle the first control column between Control A and Control B sliders and hide unused sliders
function toggleControl() {
    // Toggle the Control Group
    showSlider = showSlider === "A" ? "B" : "A";
    sessionStorage.setItem(storageKey("showSlider"), showSlider);

    // Update visibility for sliders and values
    document.querySelectorAll('tr').forEach(row => {
        const type = Number(row.dataset.type);

        // Determine visibility for each slider type
        const showLevel = showSlider === "A" && !(type <= 1 || type >= 10);
        const showCT = showSlider === "B" && (type === 3 || type === 4 || type === 5 || type >= 10);

        // Update visibility for Level sliders and values
        row.querySelectorAll('.level-slider, .level-value').forEach(el => { el.style.display = showLevel ? 'block' : 'none'; });

        // Update visibility for CT sliders and values
        row.querySelectorAll('.CT-slider, .CT-value').forEach(el => { el.style.display = showCT ? 'block' : 'none'; });
    });
}


//***********************************************  Table UI Management  ****************************************************************
//**************************************************************************************************************************************



//***********************************************  CheckBox Handling   *****************************************************************
//**************************************************************************************************************************************

function toggleRowSelection(checkbox) {
    const row = checkbox.closest('tr');
    const ID = row.dataset.ID;
    const checkboxStates = JSON.parse(sessionStorage.getItem(storageKey("checkboxStates"))) || {};    
    row.classList.toggle('selected-row', checkbox.checked);
    if (ID) checkboxStates[ID] = checkbox.checked;
    sessionStorage.setItem(storageKey("checkboxStates"), JSON.stringify(checkboxStates));
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
	// Add 'checked' class to the label
	label.classList.add('checked');
}

//Update the switches
function toggleSwitch(switchElement) {
	//console.log("Toggle command received");
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

//Starts the Polling cycle
function startPolling(url, pollResult) {
  // If polling is already active, do not start again
  if (pollingTimeoutID) {
    if (isLogging) console.log("Polling already active, not starting again.");
    return;
  }

  async function pollLoop() {
    if (!isPolling) return; // External flag that disables polling globally

    if (!isSliding) {
      try {
        if (isLogging) console.log("Polling...");
        const response = await fetch(`${url}&sessionID=${sessionID}`);
        if (!response.ok) throw new Error(`Error: ${response.status}`);
        const data = await response.json();
        pollResult(data);
      } catch (error) {
        console.error("Polling error:", error);
        stopPolling(); // Always stop if an error occurs
        return;
      }
    } else if (isLogging) {
      console.log("Polling skipped due to slider interaction");
    }

    // Schedule the next poll
    pollingTimeoutID = setTimeout(pollLoop, pollInterval);
  }

  // Start polling loop
  pollingTimeoutID = setTimeout(pollLoop, 0); // start immediately
}

//Stops the Polling
function stopPolling() {
  if (pollingTimeoutID) {
    clearTimeout(pollingTimeoutID);
    pollingTimeoutID = null;
    if (isLogging) console.log("Polling stopped");
  }
}

//This is called when the JS App window loses or regains focus. In turn it stops or starts the polling cycle.
function checkAttention() {
  const active = !document.hidden;
  const now = new Date().toLocaleTimeString('en-US', { hour12: false });
  if (isLogging) console.log(`[${now}] Attention status:`, active ? "Applet Active" : "Applet Paused");

  if (active) {
    shuttle.style.animationPlayState = "running";
    startPolling('#URL1#', pollResult);
    if (isLogging) console.log(`[${now}] Polling started (if not already running).`);
  } else {
    shuttle.style.animationPlayState = "paused";
    stopPolling();
    if (isLogging) console.log(`[${now}] Polling stopped`);
  }
}

// This is the callback function. When the polling process receives a response, it comes here and we check if there is an update pending or not.
function pollResult(data) {
    const table = document.querySelector("table");
    if (data.update) {
        if (isLogging) console.log("Update is: True");
        handleTransaction("end", table);
        initialize();
        if (!table.classList.contains('glow-EffectSuccess')) {
            table.classList.remove('glow-EffectPending');
            table.classList.add('glow-EffectSuccess');
            setTimeout(() => {
                table.classList.remove('glow-EffectSuccess');
            }, #pollUpdateDuration#);
        }
    } else {
        if (isLogging) console.log("Update is: False");
        handleTransaction("check", table);
    }
}

//Tracks where we are in a transaction.
function handleTransaction(action, table) {
    switch (action) {
        case "begin":
            transaction = Date.now();
            isLogging && console.log("Transaction started:", transaction);
            break;

        case "end":
            transaction = null;
            isLogging && console.log("Transaction finished");
            break;

        case "check":
            if (!transaction) return;
            const elapsedTime = Date.now() - transaction;
            isLogging && console.log("Elapsed time:", elapsedTime);

            if (elapsedTime > transactionTimeout && !table.classList.contains('glow-EffectFail')) {
                isLogging && console.log("Transaction is late");
                table.classList.replace('glow-EffectPending', 'glow-EffectFail');
                setTimeout(() => {
                    handleTransaction("end", table);
                    table.classList.remove('glow-EffectFail');
                    initialize();
                }, #pollUpdateDuration#);
            } else {
                isLogging && console.log("Transaction is running");
            }
            break;
    }
}

//***********************************************  Sorting   ***************************************************************************
//**************************************************************************************************************************************

//Table sort function after condensing with AI.
function sortTable(i) {
	const tbody = document.querySelector("#sortableTable tbody");
	const rows = Array.from(tbody.rows);
	const val = (cell, sel) => cell?.querySelector(sel)?.value?.toLowerCase() || cell?.textContent?.trim().toLowerCase() || "";

	//This has been modified to handle sorting when there are two different colSpan as happens when using and iFrame. The customSort column is always second from the end.
    if (isCustomSort) {
        rows.sort((a, b) => {
            const getGroupVal = row => {
                const cellIndex = row.cells.length - 2;
                return parseFloat(val(row.cells[cellIndex], 'input')) || 0;
            };
            return getGroupVal(a) - getGroupVal(b);
        });
        rows.forEach(row => tbody.appendChild(row));
        setColumnHeaders(false);
        return;
    }

	if (i === -1) {
		i = sortDirection.activeColumn;
	} else {
		sortDirection.activeColumn = i;
		sortDirection.direction = sortDirection.direction === 'asc' ? 'desc' : 'asc';
		sessionStorage.setItem(storageKey("activeColumn"), i);
	}
	const dir = sortDirection.direction;
	localStorage.setItem(storageKey("sortDirection"), JSON.stringify(sortDirection));

	rows.sort((a, b) => {
		let a1 = val(a.cells[i], i === 3 ? '.toggle-switch' : 'input');
		let b1 = val(b.cells[i], i === 3 ? '.toggle-switch' : 'input');

		if (i === 3) {
			a1 = a.cells[i]?.querySelector('.toggle-switch')?.dataset.state || a1;
			b1 = b.cells[i]?.querySelector('.toggle-switch')?.dataset.state || b1;
		}

		if (i === 3 && a1 === b1) {
			return val(a.cells[2], 'input').localeCompare(val(b.cells[2], 'input'));
		}
		return dir === 'asc' ? a1.localeCompare(b1) : b1.localeCompare(a1);
	});

	tbody.append(...rows);
	setColumnHeaders(true);
}


// Function to set or clear column header sorting classes
function setColumnHeaders(applySorting = true) {
    const headers = document.querySelectorAll('#sortableTable thead th');

    // Remove sorting indicator classes from all headers
    headers.forEach(header => { header.classList.remove('ascSort', 'descSort'); });

    // If applySorting is true, mark the active column with the correct sort class
    if (applySorting && sortDirection.activeColumn !== undefined) {
        const activeHeader = headers[sortDirection.activeColumn];
        if (activeHeader) {
            activeHeader.classList.add(sortDirection.direction === 'asc' ? 'ascSort' : 'descSort');
        }
    }
}

//***********************************************  Initialization  and Miscellaneous  **************************************************
//**************************************************************************************************************************************

//Add event listeners for control once the content is loaded
document.addEventListener("DOMContentLoaded", function() { 
	setupHeaderEventListeners();
	initialize(); 
	//Setup listeners used to pause the polling indicator when a user interacts with a slider.
	document.addEventListener("pointerdown", e => { if (e.target.matches('input[type="range"]')) { isSliding = true; shuttle.style.animationPlayState = "paused"; } });
    document.addEventListener("pointerup", e => { if (isSliding) { isSliding = false; shuttle.style.animationPlayState = "running"; } });
    document.addEventListener("pointercancel", () => { isSliding = false; shuttle.style.animationPlayState = "running"; });
	//The following listeners identify when the Window is inactive so polling can be paused.
    document.addEventListener("visibilitychange", checkAttention);
    window.addEventListener("focus", checkAttention);
    window.addEventListener("blur", checkAttention);
});

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

//Performs a complete refresh of the page. // 1000 milliseconds = 1 second
function refreshPage(timeout) { 
    // Clean up event listeners
    removeHeaderEventListeners();
    setTimeout(function() { location.reload(true);  }, timeout);    
	// Abort any ongoing fetch operations
    if (currentFetchController) {
    	currentFetchController.abort();
    	currentFetchController = null;
    }
    if (pressTimer) {
	    clearTimeout(pressTimer);
	    pressTimer = null;
    }
}

//Converts RGB string to a Hex value
function rgb2hex(rgbString) {
    return '#' + rgbString.slice(4, -1).split(',').map(num => 
        ('0' + parseInt(num.trim()).toString(16)).slice(-2)
    ).join('');
}

// Add window beforeunload event listener for cleanup
window.addEventListener('beforeunload', function() {
	// Clean up resources
	if (isLogging) { console.log("Running cleanup process.") };
	removeHeaderEventListeners();
	if (currentFetchController) currentFetchController.abort();
	if (pressTimer) clearTimeout(pressTimer);
	if (pollingInterval) clearInterval(pollingInterval);
	//transactions.clear();
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
   

