/**
*  Remote Builder SmartGrid
*  Version: See ChangeLog
*  Download: See importUrl in definition
*  Description: Used in conjunction with child apps to generate tabular reports on device data and publishes them to a dashboard.
*
*  Copyright 2025 Gary J. Milne  
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
*  See the License for the specific language governing permissions and limitations under the License.

*  License:
*  You are free to use this software in an un-modified form. Software cannot be modified or redistributed.  You may use the code for educational purposes or for use within other applications as long as they are unrelated to the 
*  production of tabular data in HTML form, unless you have the prior consent of the author. You are granted a license to use Remote Builder in its standard configuration without limits.
*  Use of Remote Builder Advanced (which includes SmartGrid) requires a license key that must be issued to you by the original developer. TileBuilderApp@gmail.com
*  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 

*  Authors Notes:
*  For more information on Remote Builder check out these resources.
*  Original posting on Hubitat Community forum: https://community.hubitat.com/t/release-remote-builder-a-new-way-to-control-devices-7-remotes-available/142060
*  Remote Builder Documentation: https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote%20Builder%20Help.pdf
*  Remote Builder - SmartGrid Documentation: https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote_Builder_SmartGrid_Help.pdf
*  Remote Builder - SmartGrid - Hard launch announcement https://community.hubitat.com/t/release-smartgrid-create-grids-of-controls-that-you-can-access-from-anywhere/148457
*
*  Remote Builder - SmartGrid - ChangeLog - See prior releases for older changelog info
*  Version 6.0.0 - Adds Thermostats as a supported Control type. Separates Fans into 3 speed and 5 speed. Add several Theme styles. Adds Alternate Row Colors. Adds experimental u1 & u2 tags under experimental for more formatting choices.
*  Version 6.0.2 - Pre-release fixes
*
*  Native Supported Sensor Types: Battery, CarbonMonoxideDetector, ContactSensor, HumidityMeasurement, MotionSensor, PowerMeter, PresenceSensor, SmokeDetector, TemperatureMeasurement, WaterSensor
*  Sensors Supported via Variables: AccelerationSensor, AirQuality, CarbonDioxideMeasurement, EnergyMeter, IlluminanceMeasurement, PressureMeasurement, ShockSensor, SignalStrength, SleepSensor, SoundPressureLevel, SoundSensor, UltravioletIndex, VoltageMeasurement
*  Known Issues: Sometimes a Shade Slider will show the value Null briefly when the slider is changed until it picks up the new value.
*  Ideas for future releases: 1) Add Media Control, 2) Remove blank fields from the data payload.
*
*  Gary Milne - July 30th, 2026 @ 4:35PM
*/

@Field static final codeDescription = "<b>Remote Builder - SmartGrid 6.0.2 (7/30/26)</b>"
@Field static final codeVersion = 602
@Field static final moduleName = "SmartGrid"

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.transform.Field
import java.text.SimpleDateFormat
import java.util.Date

//These are the data for the pickers used on the child forms.
static def textScale() { return ['50', '55', '60', '65', '70', '75', '80', '85', '90', '95', '100', '105', '110', '115', '120', '125', '130', '135', '140', '145', '150', '175', '200', '250', '300', '350', '400', '450', '500'] }
static def columnWidth() { return ['50', '60', '70', '80', '90', '100', '110', '120', '130', '140', '150', '160', '170', '180', '190', '200', '210', '220', '230', '240', '250', '260', '270', '280', '290', '300', '350', '400', '450', '500'] }
static def textAlignment() { return ['Left', 'Center', 'Right', 'Justify'] }
static def opacity() { return ['1', '0.9', '0.8', '0.7', '0.6', '0.5', '0.4', '0.3', '0.2', '0.1', '0'] }
static def elementSize() { return ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20'] }
static def elementSize2() { return ['0', '0.5', '1', '1.5', '2', '2.5', '3', '3.5', '4', '4.5', '5', '5.5', '6', '6.5', '7', '7.5', '8', '8.5', '9', '9.5', '10', '11', '11.5', '12', '12.5', '13', '13.5', '14', '14.5', '15', '15.5', '16', '16.5', '17', '17.5', '18', '18.5', '19', '19.5','20'] }
static def elementSizeMinor() { return ['0', '0.1', '0.2', '0.3', '0.4', '0.5', '0.6', '0.7', '0.8', '0.9', '1'] }
static def unitsMap() { return ['°F', ' °F', '°C', ' °C', '°']}
static def dateFormatsMap() { return [1: "To: yyyy-MM-dd HH:mm:ss.SSS", 2: "To: HH:mm", 3: "To: h:mm a", 4: "To: HH:mm:ss", 5: "To: h:mm:ss a", 6: "To: E HH:mm", 7: "To: E h:mm a", 8: "To: EEEE HH:mm", 9: "To: EEEE h:mm a", \
								10: "To: MM-dd HH:mm", 11: "To: MM-dd h:mm a", 12: "To: MMMM dd HH:mm", 13: "To: MMMM dd h:mm a", 14: "To: yyyy-MM-dd HH:mm", 15: "To: dd-MM-yyyy h:mm a", 16: "To: MM-dd-yyyy h:mm a", 17: "To: E @ h:mm a", 18: "To: MMM dd HH:mm", 19: "To: MMM dd HH:mm a"] }
static def hubProperties() { return ["sunrise", "sunrise1", "sunrise2", "sunset", "sunset1", "sunset2", "hubName", "hsmStatus", "currentMode", "firmwareVersionString", "uptime", "timeZone", "daylightSavingsTime", "currentTime", "currentTime1", "currentTime2"].sort() }
static def defaultStateMap() { return '''{"open": "[m4]Open[/m4]", "closed": "[m2]Closed[/m2]", "active": "[m4]Active[/m4]", "inactive": "Idle", "wet": "[m3]WET![/m3]", "dry": "Dry", "present": "Present", "not present": "[m4]Away[/m4]", "detected": "[m3]ALERT![/m3]", "clear": "Clear", "tested": "[m1]Tested[/m1]"}''' }

static def createDeviceTypeMap() {
    def typeMap = [ 1: "Switch", 2: "Dimmer", 3: "RGB", 4: "CT", 5: "RGBW", 10: "Valve", 11:"Lock", 13: "Garage Door", 14: "Shade", 15: "Blind", 16: "Volume", 17: "Fan3", 18: "Fan5", 19: "Thermostat", 31: "Contact", 32:"Temperature", 33:"Leak", 34:"Motion", 35:"Presence", 36:"Smoke", 37:"Carbon Monoxide", 38:"Humidity", 39:"Battery", 40:"Power", 51:"Group Row", 52:"Device Row", 53:"iFrame Row" ]
    // Create the inverse map for name-to-number lookups
    def nameToNumberMap = typeMap.collectEntries { key, value -> [value, key] }
    return [typeMap: typeMap, nameToNumberMap: nameToNumberMap]
}

static def sensorSectionToTypeMap() {
    def maps = createDeviceTypeMap()
    // Maps UI section names to the canonical type names used in createDeviceTypeMap()
    def sectionToTypeName = ["Battery": "Battery", "Carbon Monoxide": "Carbon Monoxide", "Contacts": "Contact", "Humidity": "Humidity", "Motion": "Motion", "Power": "Power", "Presence": "Presence", "Smoke": "Smoke", "Temperature": "Temperature", "Water": "Leak"]
    return sectionToTypeName.collectEntries { section, typeName -> [section, maps.nameToNumberMap[typeName]] }
}
static def sensorSectionsList() { return sensorSectionToTypeMap().keySet().sort() }
static def durationFormatsMap() { return [21: "To: Elapsed Time (dd):hh:mm:ss", 22: "To: Elapsed Time (dd):hh:mm"] }
static def invalidAttributeStrings() { return ["N/A", "n/a", " ", "-", "--", "?", "??"] }
static def devicePropertiesList() { return ["Default", "None", "battery", "colorMode", "colorName", "colorTemperature", "deviceTypeName", "energy", "healthStatus", "humidity", "ID", "lastActive", "lastActiveDuration", "lastInactive", "lastInactiveDuration", "lastSeen", "lastSeenElapsed", "network", "power", "roomName", "temperature"] }

def sensorSectionConfig() {
    return [
        Battery: [ icon: "&#128267;", stateVar: "myBattery", cap: "capability.battery", typeCode: 39, onlyReportLabel: "Only Report Low Battery", 
				extraInputs: { input(name: "minBattery", title: "<b>Low Battery</b>", type: "string", submitOnChange: true, width: 1, defaultValue: "50", newLine: true, style:"margin-right: 10px") } ],
        "Carbon Monoxide": [ icon: "&#9729;&#65039;", stateVar: "myCarbonMonoxide", cap: "capability.carbonMonoxideDetector", typeCode: 37, onlyReportLabel: "Only Report Detected CO"],
        Contacts: [icon: "&#128682;", stateVar: "myContacts", cap: "capability.contactSensor", typeCode: 31, onlyReportLabel: "Only Report Open Contacts"],
        Humidity: [icon: "&#128166;", stateVar: "myHumidity", cap: "capability.relativeHumidityMeasurement", typeCode: 38, onlyReportLabel: "Only Report Humidity Outside Range",
				extraInputs: { input(name: "minHumidity", title: "<b>Low Humidity</b>", type: "string", submitOnChange: true, width: 1, defaultValue: "50", newLine: true, style:"margin-right: 10px")
                input(name: "minHumidityColor", type: "color", title: bold("Low Humidity Color"), required: false, defaultValue: "#D4B483", submitOnChange: true, width: 1, style:"margin-right: 100px")
                input(name: "maxHumidity", title: bold("High Humidity"), type: "string", submitOnChange: true, width: 1, defaultValue: "90", newLine: false, style:"margin-right: 10px")
                input(name: "maxHumidityColor", type: "color", title: bold("High Humidity Color"), required: false, defaultValue: "#5B8DB8", submitOnChange: true, width: 1, style:"margin-right: 100px")
                input(name: "normalHumidityColor", type: "color", title: bold("Normal Humidity Color"), required: false, defaultValue: "#66BB6A", submitOnChange: true, width: 1) } ],
        Motion: [icon: "&#127939;", stateVar: "myMotion", cap: "capability.motionSensor", typeCode: 34, onlyReportLabel: "Only Report Active Motion"],
        Power: [icon: "&#9889;", stateVar: "myPower", cap: "capability.powerMeter", typeCode: 40, onlyReportLabel: "Only Report Active Devices", 
				extraInputs: {input(name: "minPower", title: "<b>Low Power</b>", type: "string", submitOnChange: true, width: 1, defaultValue: "0", newLine: true, style:"margin-right: 10px") }],
        Presence: [icon: "&#127968;", stateVar: "myPresence", cap: "capability.presenceSensor", typeCode: 35, onlyReportLabel: "Only Report Not Present"],
        Smoke: [icon: "&#128293;", stateVar: "mySmoke", cap: "capability.smokeDetector", typeCode: 36,onlyReportLabel: "Only Report Detected Smoke"],
        Temperature: [ icon: "&#127777;&#65039;", stateVar: "myTemps", cap: "capability.temperatureMeasurement", typeCode: 32, onlyReportLabel: "Only Report Temperatures Outside Range",
            	extraInputs: {input(name: "minTemp", title: "<b>Low Temp</b>", type: "string", submitOnChange: true, width: 1, defaultValue: "50", newLine: true, style:"margin-right: 10px")
                input(name: "minTempColor", type: "color", title: bold("Low Temp Color"), required: false, defaultValue: "#5BC8F5", submitOnChange: true, width: 1, style:"margin-right: 100px")
                input(name: "maxTemp", title: bold("High Temp"), type: "string", submitOnChange: true, width: 1, defaultValue: "90", newLine: false, style:"margin-right: 10px")
                input(name: "maxTempColor", type: "color", title: bold("High Temp Color"), required: false, defaultValue: "#FF4500", submitOnChange: true, width: 1, style:"margin-right: 100px")
                input(name: "normalTempColor", type: "color", title: bold("Normal Temp Color"), required: false, defaultValue: "#81C784", submitOnChange: true, width: 1) } ],
        Water: [icon: "&#128167;", stateVar: "myLeaks", cap: "capability.waterSensor", typeCode: 33, onlyReportLabel: "Only Report Wet Sensors" ]
    ]
}
							   

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
    //Does the initialization of variables at install, any variables added to the subsequent releases and checks for null variables created by when the user clicks on "No Selections".
    initialize()
    
    //Compile the JS every time to accomodate UI change requests. This compile will NOT run during normal operation of the SmartGrid, only in the edit\design phase.
    compile()

    dynamicPage(name: "mainPage", title: "<div style='text-align:center;color: #c61010; font-size:30px;text-shadow: 0 0 5px #FFF, 0 0 10px #FFF, 0 0 15px #FFF, 0 0 20px #49ff18, 0 0 30px #49FF18, 0 0 40px #49FF18, 0 0 55px #49FF18, 0 0 75px #ffffff; margin-top:-3vh !important;'>Remote Builder - " + moduleName + " 📱 </div>", uninstall: true, install: true, singleThreaded:false) {

        section(hideable: true, hidden: state.hidden.Configure, title: buttonLink('btnHideConfigure', getSectionTitle("Configure"), 20)) {
            //Setup the Table Style
            paragraph "<style>#buttons1 {font-family: Arial, Helvetica, sans-serif; width:100%; text-layout:fixed; text-align:'Center'} #buttons1 td, #buttons1 tr {width: 11%; background:#00a2ed;color:#FFFFFF;text-align:Center;opacity:0.75;padding: 8px} #buttons1 td:hover {padding: 8px; background: #27ae61;opacity:1}</style>"
            table1 = "<table id='buttons1'><td>" + buttonLink ('Controls', 'Controls', 1) + "</td><td>" + buttonLink ('Sensors', 'Sensors', 2) + "</td><td>" + buttonLink ('RenameDevices', 'Rename Devices', 3) + "</td><td>" +
                // buttonLink ('Batteries', 'Batteries', 3) + "</td><td>" + buttonLink ('Inactivity', 'Inactivity', 4) + "</td><td>" +
                buttonLink ('Endpoints', 'Endpoints', 5) + "</td><td>" + buttonLink ('Polling', 'Polling', 6) + "</td><td>" + buttonLink ('Variables', 'Variables', 7) + "</td><td>" +
                buttonLink ('GroupAndSort', 'Group & Sort', 8) + "</td><td>" + buttonLink ('Publish', 'Publish', 9) + "</td><td>" + buttonLink ('Logging', 'Logging', 10) + "</td></table>"
            paragraph table1

            if (state.activeButtonA == 1){ //Start of Controls Section
                // Input for selecting filter criteria
                input(name: "filter", type: "enum", title: bold("Filter Controls (optional)"), options: ["All Selectable Controls", "Thermostats", "Power Meters", "Switches", "Color Temperature Devices", "Color Devices", "Dimmable Devices", "Valves", "Fans", "Locks", "Garage Doors", "Shades & Blinds"].sort(), required: false, defaultValue: "All Selectable Controls", submitOnChange: true, width: 2, style:"margin-right: 100px")
                def filterOptions = [
                    "All Selectable Controls": [capability: "capability.powerMeter, capability.switch, capability.valve, capability.lock, capability.garageDoorControl, capability.doorControl, capability.fanControl, capability.audioVolume, capability.windowShade, capability.windowBlind, capability.thermostat", title: "<b>Select Controls</b>"],
                    "Thermostats": [capability: "capability.thermostat", title: "<b>Select Thermostats</b>"], "Power Meters": [capability: "capability.powerMeter", title: "<b>Select Power Meter Devices</b>"], "Switches": [capability: "capability.switch", title: "<b>Select Switch Devices</b>"], 
                    "Color Temperature Devices": [capability: "capability.colorTemperature", title: "<b>Select Color Temperature Devices</b>"], "Color Devices": [capability: "capability.colorControl", title: "<b>Select Color Devices</b>"], 
                    "Dimmable Devices": [capability: "capability.switchLevel", title: "<b>Select Dimmable Devices</b>"], "Valves": [capability: "capability.valve", title: "<b>Select Valves</b>"], "Fans": [capability: "capability.fanControl", title: "<b>Select Fans</b>"],
                    "Garage Doors": [capability: "capability.garageDoorControl", title: "<b>Select Garage Doors</b>"], "Locks": [capability: "capability.lock", title: "<b>Select Locks</b>"], "Shades & Blinds": [capability: "capability.windowShade, capability.windowBlind", title: "<b>Select Shades & Blinds</b>"]
                ]
                // Apply switch-case logic based on the filter value
                def option = filterOptions[filter]
                if (option) { input "myDevices", option.capability, title: option.title, multiple: true, submitOnChange: true, newLine: true, width: 2, style: "margin-right:25px" }
                else { if (isLogDebug) log.debug "No filter option selected." }

                paragraph line(1)
                myText = "<b>Important: If you change the selected devices you must do a " + red("Publish and Subscribe") + " for SmartGrid to work correctly.</b><br>"
            }

            if (state.activeButtonA == 2){ //Start of Sensors Section
                if (visibleSensorSections == null) app.updateSetting("visibleSensorSections", options: sensorSectionsList(), defaultValue: ["Temperature"])
                input(name: "visibleSensorSections", type: "enum", title: bold("Select Sensor Types to Display"), options: sensorSectionsList(), required: false, multiple: true, submitOnChange: true, width: 4, defaultValue: ["Temperature"], style:"margin-right: 100px")
                input(name: "btnCollapseAllSensors", type: "button", title: "Collapse All", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 1, style:"margin-top: 12px")               
                input(name: "btnExpandAllSensors", type: "button", title: "Expand All", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 1, style:"margin-top: 12px")               
                paragraph line(1)
                                
                // Build group options once from named rows
                def groupCount = customRowCount?.toInteger() ?: 0
                def groupOptions = ["None": "None"]
                if (groupCount > 0) {
                    (1..groupCount).each { i ->
                        def rowType = settings["customRowType${i}"]?.toString() ?: ""
                        // Check both "Group Row" and any variant spellings/timing issues
                        if (rowType && rowType.contains("Group Row")) {
                            // Fallback chain: try name text, strip tags, then fall back to index
                            def rawName = settings["myNameText${i}"]
                            def nameText = rawName?.toString()
                                               ?.replaceAll(/\[.*?\]/, "")
                                               ?.trim()
                            // Only skip if truly empty after cleaning
                            if (!nameText) nameText = "Group ${i}"
                            groupOptions["${i}"] = nameText
                        }
                    }
                }
                
                // Data driven approach to generating the list of Sensors Dialog.
                sensorSectionConfig().each { sectionName, cfg ->
                    if (!visibleSensorSections?.contains(sectionName)) return
                    def assignKey = "assignGroup${sectionName}"
                    def sensorVar = this."${cfg.stateVar}"
                    if (settings[assignKey] && settings[assignKey] != "None") {
                        autoAssignDevicesToGroup(settings[assignKey].toInteger(), sensorVar, cfg.typeCode)
                        app.updateSetting(assignKey, [value: "None", type: "enum"])
                    }
                    paragraph buttonLink("btnHide${sectionName}", (state.hidden?."${sectionName}" ? "${cfg.icon} ${sectionName} &#9654;" : "${cfg.icon} ${sectionName} &#9660;"), 0)
                    if (!state.hidden?."${sectionName}") {
                        input cfg.stateVar, cfg.cap, title: "<b>Select ${sectionName} Devices</b>", multiple: true, submitOnChange: true, width: 2, newLine: false, style:"margin-right: 50px"
                        input(name: "onlyReportOutsideRange${sectionName}", type: "enum", title: bold(cfg.onlyReportLabel), options: ["True", "False"], required: false, defaultValue: "False", submitOnChange: true, width: 2, style:"margin-right:50px")
                        input(name: assignKey, type: "enum", title: bold("Assign to Group"), options: groupOptions, defaultValue: "None", submitOnChange: true, width: 2, style:"margin-right:50px")
                        cfg.extraInputs?.call()
                    }
                    paragraph line(1)
                }
            }

            if (state.activeButtonA == 3){ //Start of Rename Devices Section
                if (myDeviceRenameCount == null) app.updateSetting("myDeviceRenameCount", [value: "10", type: "enum"])
                input(name: "myDeviceRenameCount", title: "<b>Device Rename Count?</b>", type: "enum", options: ['0', '2', '4', '6', '8', '10', '12', '14', '16', '18', '20'], submitOnChange: true, defaultValue: "0", width: 2)
                input(name: "appendTypeSensorSections", type: "enum", title: bold("Append Sensor Type to Duplicate Sensor Names"), options: sensorSectionsList(), required: false, multiple: true, submitOnChange: true, width: 4, defaultValue: [], style:"margin-right: 100px")

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
                input(name: "rebuildEndpoints", type: "button", title: "Rebuild Endpoints", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, style:"margin-top:12px; margin-left:20px")
                paragraph line(1)

                paragraph "<a href='${state.localEndpoint}' target=_blank><b>Local Endpoint</b></a>: ${state.localEndpoint} "
                paragraph "<a href='${state.cloudEndpoint}' target=_blank><b>Cloud Endpoint</b></a>: ${state.cloudEndpoint} "

                paragraph line(1)
                myText = "<b>Important: If these endpoints are not generated you may have to enable OAuth in the child application code for this application to work.</b><br>"
                myText += "Both endpoints can be active at the same time and can be enabled or disable through this interface.<br>"
                myText += "Endpoints are paused if this instance of the <b>Remote Builder</b> application is paused. Endpoints are deleted if this instance of <b>Remote Builder</b> is removed.<br>"
                paragraph summary("Endpoint Help", myText)
                paragraph line(1)
                myText = "<b>You can use these strings to create embedded content within a web page. Just replace any [] with <>.</b><br>"
                myText += "<ul><li>[iframe name=" + state.AppID + "-A src=" + state.localEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe]</li>"
                myText += "<li>[iframe name=" + state.AppID + "-A src=" + state.cloudEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe]</li>"
                myText += "<li>[iframe name=" + state.AppID + "-B src=" + state.localEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe]</li>"
                myText += "<li>[iframe name=" + state.AppID + "-B src=" + state.cloudEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe]</li></ul>"
                paragraph summary("Embedded Links", myText)
            }

            if (state.activeButtonA == 6){ //Start of Polling Section
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
                myText += "<b>Note: </b> You can initiate a full refresh of the table at anytime regardless of the polling interval using the Refresh Icon <b>?</b>."
                paragraph summary("Polling Help", myText)
                paragraph red("<b>Important: The Polling Enabled\\Disabled and Poll Interval only apply to the first time a SmartGrid is run in a browser. After that you must change these settings using the Modal window which is accessible by holding a mouse or finger down within the SmartGrid for 4 seconds.</b>")
            }

            if (myVariableCount == null) app.updateSetting("myVariableCount", [value: "0", type: "enum"])
            if (state.activeButtonA == 7){ //Start of Variables
                input name: "myVariableCount", title: "<b>Source Count?</b>", type: "enum", options: (0..12), submitOnChange: true, defaultValue: 0, style: "width:12%"
                def variableCount = myVariableCount?.toInteger() ?: 0
                for (int i = 1; i <= variableCount; i++) {
                    def sourceSetting = settings["variableSource${i}"] ?: "Device Attribute"
                    def sourceWidth = (sourceSetting == "Hub Variable" || sourceSetting == "Hub Property") ? "width:20.75%" : "width:10%"
                    input "variableSource${i}", "enum", title: dodgerBlue("<b>Source #$i</b>"), options: ["Device Attribute", "Hub Variable", "Hub Property", "Disabled"], submitOnChange: true, defaultValue: "Device Attribute", newLine: true, style: sourceWidth
                    def sourceType = sourceSetting
                    if (sourceType == "Device Attribute") {
                        def devKey = "myDevice${i}"
                        def dev = settings[devKey]
                        input devKey, "capability.*", title: "<b>Device</b>", multiple: false, required: false, submitOnChange: true, newLine: false, style: "margin-left:20px;width:10%"
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
                        (1..5).each { j ->
                            def attrIndex = i * 10 + j
                            input "myHubVariable${attrIndex}", "enum", title: "<b>Hub Variable (%var${attrIndex}%)</b>", submitOnChange: true, options: varList, newLine: false, style: "margin-left:20px;width:10%"
                        }
                    }
                    if (sourceType == "Hub Property") {
                        def varList = getAllGlobalVars().findAll { it.value?.type }?.keySet()?.collect()?.sort { it.capitalize() }
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
                myText += "Once you have configured your variables you can place them within the SmartGrid using the Group & Sort using the form <b>%varX%</b>.<br>"
                paragraph summary("Variables Help", myText)
                paragraph "<b>Note:</b> A change in the value of a variable will cause the SmartGrid to refresh it's data. Choose wisely."
                paragraph red("<b>Important: Custom rows are only displayed when Custom Sort is enabled.</b>")
            }
            
            if (state.activeButtonA == 8){ //Start of Group & Sort
                input(name: "isCustomSort", type: "enum", title: bold("Enable Groups and Custom Sort"), options: ['true', 'false'], required: false, defaultValue: "false", submitOnChange: true, width:2, style:"margin-right: 50px;")
                myText = "When you enable Groups and Custom Sort you can rearrange grid as follows:<br>"
                myText += "<b>Step 1:</b> Add Groups as needed.<br>"
                myText += "<b>Step 2:</b> Enable Drag & Drop.<br>"
                myText += "<b>Step 3:</b> Reorder the rows to your liking in the grid below using drag and drop.<br>"
                myText += "<b>Step 4:</b> Save the sort order. (Drag and Drop will then be disabled)"
                paragraph summary("Custom Groups and Sort Help", myText)
                paragraph line(1)
                if (isCustomSort == "true"){                    
                    input (name: "customRowCount", title: "<b>How Many Custom Groups\\Rows?</b>", type: "enum", options: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20], submitOnChange:true, width:2, defaultValue: 0)
                    if (customRowCount.toInteger() > 0) {
                        def slurper = new groovy.json.JsonSlurper()
                        def sortOrder = slurper.parseText(state.customSortOrder ?: "[]")
                        def separators = sortOrder.findAll { it.containsKey("UID") && it.UID?.toString().endsWith("-51") }.sort { it.row }
                        def rowOptions = ["All": "All"]
                        (1..customRowCount.toInteger()).each { i ->
                            def nameText = settings["myNameText${i}"]?.toString() ?.replaceAll(/\[.*?\]/, "")?.trim() ?: "Row ${i}"
                            def rowType   = settings["customRowType${i}"]?.toString() ?: "Unknown"
                            def visualPos = separators.findIndexOf { it.UID.toString().tokenize("-")[0].toInteger() == i }
                            def posLabel  = visualPos >= 0 ? " [pos:${visualPos + 1}]" : ""
                            rowOptions["${i}"] = "${i} - ${nameText}${posLabel}"
                        }
                        input (name: "displayCustomRow", title: "<b>Display Group\\Row</b>", type: "enum", options: rowOptions, submitOnChange: true, width: 2, defaultValue: "All")
                    }

                    for (int i = 1; i <= customRowCount.toInteger(); i++) {
                        if (displayCustomRow != null && displayCustomRow != "All" && displayCustomRow != "${i}") continue
                        if (settings["showInfoColumnControls${i}"] == null) { app.updateSetting("showInfoColumnControls${i}", [value: false, type: "bool"]) }
                        input (name: "customRowType${i}", title: "<b>Group\\Row $i</b>", type: "enum", options: ["Device Row", "Group Row", "iFrame Row", "Disabled"], submitOnChange:true, newLine: true, width:1, defaultValue: "Disabled", style:"margin-right: 25px")
                        if (settings["customRowType${i}"] == "Device Row") {
                            input "myNameText${i}", "string", title: "<b>Name Column Text</b>", defaultValue: "[b]Your Text Here (%var%)[/b]", submitOnChange: false, width: 2, newLine: false, style: "margin-right: 25px"
                            input "myStateText${i}", "string", title: "<b>State Column Text</b>", defaultValue: "", submitOnChange: false, width: 2, newLine: false, style: "margin-right: 25px"
                            input "myControlABText${i}", "string", title: "<b>Control A/B Column Text</b>", defaultValue: "", submitOnChange: false, width: 1, newLine: false, style: "margin-right: 25px"
                            input "myControlCText${i}", "string", title: "<b>Control C Column Text</b>", defaultValue: "", submitOnChange: false, width: 1, newLine: false, style: "margin-right: 25px"
                            input "myInfoAText${i}", "string", title: "<b>Info 1 Text</b>", defaultValue: "", submitOnChange: false, width: 1, newLine: false, style: "margin-right: 25px"
                            input "myInfoBText${i}", "string", title: "<b>Info 2 Text</b>", defaultValue: "", submitOnChange: false, width: 1, newLine: false, style: "margin-right: 25px"
                            input "myInfoCText${i}", "string", title: "<b>Info 3 Text</b>", defaultValue: "", submitOnChange: false, width: 1, newLine: false, style: "margin-right: 25px"
                        }
                        if (settings["customRowType${i}"] == "Group Row") {
                            input "myNameText${i}", "string", title: "<b>Name Column Text</b>", defaultValue: "[b]Your Text Here (%var%)[/b]", submitOnChange: false, width: 2, newLine: false, style: "margin-right: 25px"
                            input "myStateText${i}", "string", title: "<b>State Column Text</b>", defaultValue: "", submitOnChange: false, width: 2, newLine: false, style: "margin-right: 25px"
                            input "myControlABText${i}", "string", title: "<b>Control A/B Column Text</b>", defaultValue: "", submitOnChange: false, width: 2, newLine: false, style: "margin-right: 25px"
                            input "myControlCText${i}", "string", title: "<b>Control C Column Text</b>", defaultValue: "", submitOnChange: false, width: 2, newLine: false, style: "margin-right: 25px"
                            input "showInfoColumnControls${i}", "bool", title: "<b>Show Info Column Controls</b>", defaultValue: false, submitOnChange: true, width: 2, newLine: false
                            if (settings["showInfoColumnControls${i}"] == true) {
                                input "myInfoAText${i}", "string", title: "<b>Info 1 Text</b>", defaultValue: "", submitOnChange: false, width: 2, newLine: true, style: "margin-right: 25px"
                                input "info1SourceGroup${i}", "enum", title: bold("Info 1 Source"), defaultValue: "Default", required: false, multiple: false, options: devicePropertiesList(), submitOnChange: true, width: 1, newLine: false, style: "margin-right: 25px"
                                input "myInfoBText${i}", "string", title: "<b>Info 2 Text</b>", defaultValue: "", submitOnChange: false, width: 2, newLine: false, style: "margin-right: 25px"
                                input "info2SourceGroup${i}", "enum", title: bold("Info 2 Source"), defaultValue: "Default", required: false, multiple: false, options: devicePropertiesList(), submitOnChange: true, width: 1, newLine: false, style: "margin-right: 25px"
                                input "myInfoCText${i}", "string", title: "<b>Info 3 Text</b>", defaultValue: "", submitOnChange: false, width: 2, newLine: false, style: "margin-right: 25px"
                                input "info3SourceGroup${i}", "enum", title: bold("Info 3 Source"), defaultValue: "Default", required: false, multiple: false, options: devicePropertiesList(), submitOnChange: true, width: 1, newLine: false, style: "margin-right: 25px"
                            }
                        }
                        if (settings["customRowType${i}"] == "iFrame Row") {
                            input "myStateText${i}", "string", title: "<b>iFrame URL in form http://www.example.com</b>", defaultValue: "[b]Your Text Here (%var%)[/b]", submitOnChange: false, width: 5, newLine: false, style: "margin-right: 45px"
                            input "myIFrameHeight${i}", "string", title: "<b>iFrame Height (px)</b>", defaultValue: "200", submitOnChange: false, width: 1, newLine: false, style: "margin-right: 25px"
                        }
                        paragraph line(1)
                    }
                    input(name: "refresh", type: "button", title: "Apply Changes", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2)
                    paragraph line(1)
                    myText = "Here you can configure custom lines that can be placed within the table when using a <b>Custom Sort</b>. These are generally intended as groups between functional groups within a table.<br>"
                    myText += "<b>Name Column Text X:</b> This text will be placed within the <b>Name</b> column.<br>"
                    myText += "<b>State Column Text X:</b> If configured this value will be displayed within the <b>State</b> column.<br>"
                    myText += "You can place static text, HTML text using [] or variables within this text. To access a variable just enter %varX% where X is the variable number defined within the <b>Variables</b> tab.<br>"
                    myText += "To use a blank value for a field simply use the space bar to remove the default values."
                    paragraph summary("Custom Row Help", myText)
                    paragraph line(2)               
                    
                    myText = "<b>Notes:</b><br>"
                    myText += "1) <b>If you add devices or sensors to your SmartGrid you must update your Custom Sort Order to include the new lines.</b><br>"
                    myText += "2) " + red("<b>DO NOT EXECUTE ANY COMMANDS OR REFRESH YOUR SCREEN UNTIL YOU HAVE SAVED YOUR CUSTOM SORT OR YOUR PROGRESS WILL BE LOST.</b>")
                    paragraph myText
            	}     
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
                myText += "<b>Use these Remotes when you wish to run two instances of the same SmartGrid on the same browser page, such as within a Hubitat dashboard. This allows each instance to have their own unique local settings. See documentation for more information.</b>"
                paragraph myText
                paragraph line(1)

                if (myRemoteName) app.updateLabel(myRemoteName)
                myText =  "Publishing a remote is optional and only required if it will be used within a Hubitat dashboard. Remotes can be accessed directly via the URL's in the Endpoints section and bypass the Dashboard entirely if desired.<br>"
                myText += "The <b>Event Timeout</b> period is how long Tile Builder will wait for subsequent events before publishing the table. Lowering the event timeout will make the table more responsive but also increase the number of refreshes. "
                myText += "Re-publishing a table will cause it to refresh on the dashboard unless the Event Timeout is set to Never. In this situation you can synchronise the table using the <b>Refresh Icon</b> to synchronise the table.<br>"
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
        }

        //Start of Preview Section
        section(hideable: true, hidden: state.hidden.Preview, title: buttonLink('btnHidePreview', getSectionTitle("Preview"), 20)) {
            input(name: "displayEndpoint", type: "enum", title: bold("Display Endpoint"), options: ["Local", "Cloud"], required: false, defaultValue: "Local", submitOnChange: true, width: 1, style:"margin-right:25px")
            input(name: "tilePreviewWidth", type: "enum", title: bold("Max Width (x200px)"), options: [1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8], required: false, defaultValue: 2, submitOnChange: true, style: "width:12%;margin-right:25px")
            input(name: "tilePreviewHeight", type: "enum", title: bold("Preview Height (x190px)"), options: [1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8], required: false, defaultValue: 2, submitOnChange: true, style: "width:12%;margin-right:25px")
            input(name: "tilePreviewBackground", type: "color", title: bold("Preview Background Color"), required: false, defaultValue: "#000000", width: 2, submitOnChange: true, style: "margin-right:25px")
            if (myRemoteName != null && myRemote != null && state.deviceList != null) input(name: "publishSubscribe", type: "button", title: "Publish and Subscribe", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, style:"margin-top:12px;margin-right:25px")
            else input(name: "cannotPublish", type: "button", title: "Publish & Subscribe", backgroundColor: "#D3D3D3", textColor: "white", submitOnChange: false, width: 2, style:"margin-top:12px;margin-right:25px")
            
            if (isCustomSort == "true"){
                if (isDragDrop) input(name: "EnableDragDrop", type: "button", title: "Enable Drag & Drop", backgroundColor: "orange", textColor: "white", submitOnChange: true, width: 1, style:"margin-left: 25px; margin-top: 12px;")
                else input(name: "EnableDragDrop", type: "button", title: "Enable Drag & Drop", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 1, style:"margin-left: 25px; margin-top: 12px;")
                if (isDragDrop) { input(name: "saveCustomSort", type: "button", title: " Save  Custom  Sort ", backgroundColor: "green", textColor: "white", submitOnChange: true, width: 1, style:"margin-left: 25px; margin-top: 12px;")
                                 paragraph red("<b>When Drag and Drop is enabled all devices are temporarily made visible. You can click on the section header Title to sort the contents of a section.</b>")
                                }
                else input(name: "saveCustomSort", type: "button", title: "Save  Custom  Sort", backgroundColor: "#D3D3D3", textColor: "white", submitOnChange: true, width: 1, style:"margin-left: 25px; margin-top: 12px;")
            }
            
            myMaxWidth = ( (tilePreviewWidth.toFloat() * 210) - 10 ) + 3 * 2
            myMaxHeight = ( (tilePreviewHeight.toFloat() * 200) - 10 ) + 3 * 2

            if (displayEndpoint == "Local") paragraph """<div style="margin-left: 25px; background-color: ${tilePreviewBackground}; padding: 10px; border: 2px solid black; border-radius: 10px;">
                <iframe name="${state.AppID}-P" src="${state.localEndpoint}" width="${myMaxWidth.toInteger()}" height="${myMaxHeight.toInteger()}" style="padding: 0px; background-color: ${tilePreviewBackground}; border: 3px dashed white; border-radius: 10px;" scrolling="no"></iframe>
                </div>"""
            if (displayEndpoint == "Cloud") paragraph """<div style="margin: 25px; background-color: ${tilePreviewBackground}; padding: 10px; border: 2px solid black; border-radius: 10px;">
                <iframe name="${state.AppID}-P" src="${state.cloudEndpoint}" width="${myMaxWidth.toInteger()}" height="${myMaxHeight.toInteger()}" style="padding: 0px; background-color: ${tilePreviewBackground}; border: 3px dashed white; border-radius: 10px;" scrolling="no"></iframe>
                </div>"""
            myText = "The preview window above is optimized for the default Hubitat Dashboard tile size of 200px wide by 190px tall. Tiles greater than 1x1 will be slightly larger than direct multiples of this number because space previously allocated between tiles is now part of the tile.<br>"
            myText += "If you wish to maximize your tile space by shrinking the gap between tiles you can change the <b>Grid Gap</b> using the Dashboard Grid menu. Or you could use the following CSS: <br>"
            myText += "<mark>[class*='tile-title']{height:0% !important; visibility:hidden;}</mark>    <mark>[class*='tile-contents']{width:100% !important; height:100% !important; padding:0px;}</mark><br>"
            myText += "To help visualize the dashboard tile edges and add an orange dashed outline to each tile you could use CSS like this:<br><mark>[class*='tile-primary']{outline: 1px dashed orange;}</mark>"
            paragraph summary("Preview Notes", myText)
        }

        //Start of Design Section
        section(hideable: true, hidden: state.hidden.Design, title: buttonLink('btnHideDesign', getSectionTitle("Design"), 20)) {
            paragraph "<style>#buttons2 {font-family: Arial, Helvetica, sans-serif; width:100%; text-layout:fixed; text-align:'Center'} #buttons2 td, #buttons2 tr {width: 10%; background:#00a2ed;color:#FFFFFF;text-align:Center;opacity:0.75;padding: 4px} #buttons2 td:hover {padding: 4px; background: #27ae61;opacity:1}</style>"
            table2 = "<table id='buttons2'><td>" + buttonLink ('General', 'General', 21) + "</td><td>" + buttonLink ('Appearance', 'Appearance', 22) + "</td><td>" + buttonLink ('Title', 'Title', 23) + "</td><td>" +
                buttonLink ('Columns', 'Columns', 24) + "</td><td>" + buttonLink ('Padding', 'Padding', 25) + "</td><td>" + buttonLink ('Advanced', 'Advanced', 26) + "</td><td>" + buttonLink ('Experimental', 'Experimental', 27) + "</td></table>"
            paragraph table2

            if (state.activeButtonB == 21){ //General
                input(name: "defaultDateTimeFormat", title: bold("Date Time Format"), type: "enum", options: dateFormatsMap(), submitOnChange: true, defaultValue: 3, width: 2, style:"margin-right:25px")
                input(name: "defaultDurationFormat", title: bold("Duration Format"), type: "enum", options: durationFormatsMap(), submitOnChange: true, defaultValue: 21, width: 2, style:"margin-right:25px")
                input(name: "controlSize", title: bold("Control Size"), type: "enum", options: ["7.5", "10", "12.5", "15", "17.5", "20", "22.5", "25", "27.5", "30"], submitOnChange: true, defaultValue: "15", width: 2, style:"margin-right:25px")
                input (name: "ha", type: "enum", title: bold("Horizontal Alignment"), required: false, options: ["Stretch", "Left", "Center", "Right"], defaultValue: "Stretch", submitOnChange: true, width: 2, style:"margin-right:25px", newLine: true)
                input(name: "invalidAttribute", title: bold("Invalid Attribute String"), type: "enum", options: invalidAttributeStrings(), submitOnChange: true, defaultValue: "N/A", width: 2, style:"margin-right:25px", newLine:true)
                input ("tempUnits", "enum", title: "<b>Temperature Units</b>", options: unitsMap(), multiple: false, submitOnChange: true, width: 2, required: false, defaultValue: "°F", style:"margin-right:25px")
                input ("tempDecimalPlaces", "enum", title: "<b>Decimal Places</b>", options: ["0 Decimal Places", "1 Decimal Place"], multiple: false, defaultValue: "0 Decimal Places", submitOnChange: true, width: 2, required: false, style:"margin-right:25px")
                //input ("capitalizeStrings", "enum", title: "<b>Capitalize Variable Strings</b>", options: ["True", "False"], multiple: false, defaultValue: "False", submitOnChange: true, width: 2, required: false)
                input(name: "thermDisplay", type: "enum", title: bold("Thermostat Display"), defaultValue: "1", width: 2, submitOnChange: true, options: ["1":"Mode • Setpoint • State", "2":"Mode • Setpoint", "3":"Mode • State", "4":"State", "5":"State • Setpoint"], newLine:true, style:"margin-right:25px")
                input(name: "thermTempDisplay", type: "enum", title: bold("Thermostat Temp"), defaultValue: "1", width: 2, submitOnChange: true, options: ["1":"Temperature", "2":"Temperature|Humidity"])
                input(name: "sortHeaderHintAZ", type: "color", title: bold("Sort Header Hint A-Z"), required: false, defaultValue: "#00FF00", submitOnChange: true, width: 2, style:"margin-right:25px", newLine: true)
                input(name: "sortHeaderHintZA", type: "color", title: bold("Sort Header Hint Z-A"), required: false, defaultValue: "#FF0000", submitOnChange: true, width: 2, style:"margin-right:25px")
            }

            if (state.activeButtonB == 22){ //Appearance
                input(name: "hts", type: "enum", title: bold("Header Text Size %"), options: textScale(), required: false, defaultValue: "125", width: 2, submitOnChange: true, style:"margin-right:25px; margin-left:20px")
                input(name: "htc", type: "color", title: bold2("Header Text Color", htc), required: false, defaultValue: "#000000", width: 2, submitOnChange: true)
                input(name: "hbc", type: "color", title: bold2("Header Background Color", hbc), required: false, defaultValue: "#2375b8", width: 2, submitOnChange: true)
                input(name: "hbo", type: "enum", title: bold("Header Opacity"), options: opacity(), required: false, defaultValue: "1", width:2, submitOnChange: true)
                paragraph line(2)
                input (name: "crts", type: "enum", title: bold("Group Row Text Size %"), options: textScale(), required: false, defaultValue: "100", submitOnChange: true, width: 2, style:"margin-right:25px; margin-left:20px")
                input (name: "crtc", type: "color", title: bold2("Group Row Text Color", crtc), required: false, defaultValue: "#000000", submitOnChange: true, width: 2)
                input (name: "crbc", type: "color", title: bold2("Group Row Background Color #1", crbc), required: false, defaultValue: "#b6d7f1", submitOnChange: true, width: 2)
                input (name: "crbc2", type: "color", title: bold2("Group Row Background Color #2", crbc2), required: false, defaultValue: "#2375b8", submitOnChange: true, width: 2)
                paragraph line(2)
                input(name: "rts", type: "enum", title: bold("Device Row Text Size %"), options: textScale(), required: false, defaultValue: "100", submitOnChange: true, width:2, style:"margin-right:25px; margin-left:20px")
                input(name: "rtc", type: "color", title: bold2("Device Row Text Color", rtc), required: false, defaultValue: "#000000", submitOnChange: true, width:2)
                input(name: "rbc", type: "color", title: bold2("Device Row Background Color", rbc), required: false, defaultValue: "#b6d7f1", submitOnChange: true, width:2)
                input(name: "rbo", type: "enum", title: bold("Device Row Background Opacity"), options: opacity(), required: false, defaultValue: "1", submitOnChange: true, width:2)
                input(name: "sfw", type: "enum", title: bold("State Font Weight"), options: ["Normal","Bold"], required: false, defaultValue: "Normal", submitOnChange: true, width:2)
                input (name: "isAlternateRows", type: "enum", title: bold("Use Alternate Row Colors?"), options: ["True", "False"], required: false, defaultValue: "False", submitOnChange: true, width: 2, newLine:true, style:"margin-right:25px; margin-left:20px;")
                if (isAlternateRows == "True"){
                    input (name: "ratc", type: "color", title: bold2("Alt. Device Row Text Color", ratc), required: false, defaultValue: "#000000", submitOnChange: true, width: 2)
                    input (name: "rabc", type: "color", title: bold2("Alt. Device Row Background Color", rabc), required: false, defaultValue: "#E9F5CF", submitOnChange: true, width: 2)
                }   
                paragraph line(2)
                input(name: "highlightSelectedRows", type: "enum", title: bold("Highlight Selected Rows"), options: ["True", "False"], required: false, defaultValue: "True", submitOnChange: true, width: 2, newLine: true, style:"margin-right:25px; margin-left:20px")
                if (highlightSelectedRows == "True") input(name: "rbs", type: "color", title: bold2("Selected Row Background Color", rbs), required: false, defaultValue: "#fdf09b", submitOnChange: true, width:2)
                paragraph line(2)
                input (name: "bc", type: "color", title: bold2("Border Color", bc), required: false, defaultValue: "#000000", submitOnChange: true, width: 2, style:"margin-right:25px; margin-left: 20px;")
                input (name: "bwo", type: "enum", title: bold('Border Width - Outer'), options: elementSize2(), required: false, defaultValue: '2.5', width: 2, submitOnChange: true, style:"margin-right:25px;")
                input (name: "bwi", type: "enum", title: bold('Border Width - Inner'), options: elementSize2(), required: false, defaultValue: '2', width: 2, submitOnChange: true)
                paragraph line(2)
                input (name: "theme", type: "enum", title: bold('Select Color Theme'), options: ['Blue','Brown','Green','Grey','Modern','Mono','Orange','Pink','Purple','Rose','Sand'].sort(), required: false, defaultValue: 'Blue', width: 2, submitOnChange: true)
                input (name: "applyTheme", type: "button", title: "Apply Theme", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, style:"margin-left:20px;margin-top:12px;")
            }

            if (state.activeButtonB == 23){ //Title
                input(name: "tt", type: "string", title: bold("Title Text (? to disable)"), required: false, defaultValue: "[b]My Title[/b]?", submitOnChange: true, width: 2, style:"margin-right:25px")
                input(name: "ts", type: "enum", title: bold("Text Size %"), options: textScale(), required: false, defaultValue: "150", submitOnChange: true, width: 2, style:"margin-right:25px")
                input(name: "tp", type: "enum", title: bold("Text Padding"), options: elementSize(), required: false, defaultValue: "5", submitOnChange: true, width: 2, style:"margin-right:25px")
                input(name: "ta", type: "enum", title: bold("Text Alignment"), options: textAlignment(), required: false, defaultValue: "Center", submitOnChange: true, width: 2, style:"margin-right:25px")
                input(name: "tc", type: "color", title: bold2("Text Color", tc), required: false, defaultValue: "#000000", submitOnChange: true, width: 2, newLine:true, style:"margin-right:25px")
                input(name: "tb", type: "color", title: bold2("Background Color", tb), required: false, defaultValue: "#a09fce", submitOnChange: true, width: 2, style:"margin-right:25px")
                input(name: "to", type: "enum", title: bold("Background Opacity"), options: opacity(), required: false, defaultValue: "1", submitOnChange: true, width:2, style:"margin-right:25px")
            }

            if (state.activeButtonB == 24){ //Columns
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
                input (name: "tpad", type: "enum", title: bold('Table Edge Padding'), options: elementSize(), required: false, defaultValue: '1', width: 2, submitOnChange: true, style:"margin-right:25px")
                input (name: "tmt", type: "enum", title: bold("Top Margin"), options: elementSize(), required: false, defaultValue: "0", submitOnChange: true, width: 2, style:"margin-right:25px")
                paragraph line(1)
                input (name: "thp", type: "enum", title: bold("Column Horizontal Padding"), options: elementSize(), required: false, defaultValue: 5, submitOnChange: true, width: 2, style:"margin-right:25px")
                input (name: "tvp", type: "enum", title: bold("Row Vertical Padding - Major"), options: elementSize(), required: false, defaultValue: 3, submitOnChange: true, width: 2, style:"margin-right:25px")
                input (name: "tvpm", type: "enum", title: bold("Row Vertical Padding - Minor"), options: elementSizeMinor(), required: false, defaultValue: "0", submitOnChange: true, width: 2, style:"margin-right:25px")
            }

            if (state.activeButtonB == 26){ //Advanced
                if (markTag == null) app.updateSetting("markTag", [value: "background-color:yellow; color:black; padding:0.05em 0.25em; border-radius:0.3em; outline:1px dotted #000000; font-weight:bold;", type: "text"])
                if (m1Tag == null) app.updateSetting("m1Tag", [value: "background-color:dodgerBlue; color:white; padding:0.1em 0.4em;border-radius: 0.4em;outline: 1px dashed black; font-weight:bold;", type: "text"])
                if (m2Tag == null) app.updateSetting("m2Tag", [value: "background-color:lime; color:black; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", type: "text"])
                if (m3Tag == null) app.updateSetting("m3Tag", [value: "background-color:indianRed; color:white; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", type: "text"])
                if (m4Tag == null) app.updateSetting("m4Tag", [value: "background-color:orange; color:white; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", type: "text"])
                if (m5Tag == null) app.updateSetting("m5Tag", [value: "background-color:gray; color:white; padding:0.1em 0.4em; border-radius:0.4em; outline:1px dashed black; font-weight:bold;", type: "text"])

                def part1 = "<b>Here you can configure some CSS style tags that you can reference in your SmartGrid to draw attention to a value.</b> Use something like: <b><mark> [m1]%var32%[/m1] </mark></b><br>"
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
                part2 = "Examples of these are <mark> open, closed, active, inactive, wet, dry, present, not present </mark> etc. For example 'open' could be changed to 'Abierto'"
                paragraph part1 + part2
                input (name: "deviceStateMap", type: "text", title: "<b>Modify the values in the map below to change the values displayed in the State column. Clear string to restore the default values. Use tags in the form [m1]Active[/m1]</b>", required: false, defaultValue: '''{"open": "open", "closed": "closed", "active": "active", "inactive": "inactive", "wet": "wet", "dry": "dry", "present": "present", "not present": "not present", "detected": "detected", "clear": "clear", "tested": "tested"}''', width:12, submitOnChange:true, style: "border-radius: 0.3em; outline: 2px solid #000000")
            }

            if (state.activeButtonB == 27){ //Experimental
                if (u1Tag == null) app.updateSetting("u1Tag", [value: "font-size:0.6em !important;", type: "text"])
                if (u2Tag == null) app.updateSetting("u2Tag", [value: "font-size:0.8em !important;", type: "text"])
                
                paragraph "<b>You will find experimental settings here if there are any.</b>"
                def part1 = "<b>Here you can configure some CSS style tags that are user specific. Use them to make any text appear different. </b> Use something like: [u1]Header Title[/u1] to change the size for example.</b><br>"
                def part2 = "You often do not need to add the closing tags but if you have any formatting issues you can add them. To restore the default value for a tag just delete the contents of the field.<br>"
                paragraph part1 + part2
				input (name: "u1Tag", type: "text", title: bold("Enter or Edit a String for the CSS formatting of the <mark> [u1] </mark> HTML tag."), required: false, newLine: false, defaultValue: "font-size:0.6em !important;", width:6, submitOnChange:true, style: "border-radius: 0.3em; outline: 2px solid #000000")
                input (name: "u2Tag", type: "text", title: bold("Enter or Edit a String for the CSS formatting of the <mark> [u2] </mark> HTML tag."), required: false, newLine: false, defaultValue: "font-size:0.8em !important;", width:6, submitOnChange:true, style: "border-radius: 0.3em; outline: 2px solid #000000")
            }
            paragraph line(1)
            paragraph "<b>Important: You must do a " + red("Publish and Subscribe") + " for SmartGrid to receive updates and work correctly in polling mode or to update automatically in the above window!</b><br>"
        }

        //Start of Footer Section
        section {
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

//***************************************************  Standard System Elements  *****************************************************
//Configures all of the default settings values. This allows us to have some parts of the settings not be visible but still have their values initialized.
//We do this to avoid errors that might occur if a particular setting were referenced but had not been initialized.
def initialize() {
    if (state.initialized != true) {
        if (isLogTrace) log.trace("<b>initialize: Initializing all variables.</b>")
        
        // Device Selection
        app.updateSetting("filter", [value: "All Selectable Controls", type: "enum"])
        
        // Endpoints
        createAccessToken()
        state.localEndpoint = "${getFullLocalApiServerUrl()}/tb?access_token=${state.accessToken}"
        state.cloudEndpoint = "${getFullApiServerUrl()}/tb?access_token=${state.accessToken}"
        state.localEndpointData = "${getFullLocalApiServerUrl()}/tb/data?access_token=${state.accessToken}"
        state.cloudEndpointData = "${getFullApiServerUrl()}/tb/data?access_token=${state.accessToken}"
        state.localEndpointPoll = "${getFullLocalApiServerUrl()}/tb/poll?access_token=${state.accessToken}"
        state.cloudEndpointPoll = "${getFullApiServerUrl()}/tb/poll?access_token=${state.accessToken}"
                
        // All first-time settings in one map: [settingName: [value, type]]
        def firstTimeSettings = [
            // Polling
            isPolling: ["Enabled", "enum"], pollInterval: ["3", "enum"],
            pollUpdateColorSuccess: ["#00FF00", "color"], pollUpdateColorFail: ["#FF0000", "color"],
            pollUpdateColorPending: ["#FFA500", "color"], pollUpdateDuration: ["2", "enum"],
            shuttleColor: ["#99C5FF", "color"], shuttleHeight: ["3", "enum"],
            // Preview
            tilePreviewWidth: ["3", "enum"], tilePreviewHeight: ["2", "enum"],
            tilePreviewBackground: ["#696969", "color"],
            // General
            invalidAttribute: ["N/A", "enum"], defaultDateTimeFormat: ["3", "enum"],
            defaultDurationFormat: ["21", "enum"], controlSize: ["15", "enum"],
            tempUnits: ["°F", "enum"], sortHeaderHintAZ: ["#00FF00", "color"],
            sortHeaderHintZA: ["#FF0000", "color"], tempDecimalPlaces: ["0 Decimal Places", "enum"],
            // Table Layout
            thp: ["5", "enum"], tvp: ["3", "enum"], tvpm: ["0", "enum"], tmt: ["0", "enum"],
            ha: ["Stretch", "enum"], va: ["Center", "enum"],
            // Attribute Colors
            minTempColor: ["#5BC8F5", "color"], maxTempColor: ["#FF4500", "color"], normalTempColor: ["#81C784", "color"],
            minHumidityColor: ["#D4B483", "color"], maxHumidityColor: ["#5B8DB8", "color"], normalHumidityColor: ["#66BB6A", "color"],
            // Column Headers
            column2Header: ["Icon", "text"], column3Header: ["Name", "text"],
            column4Header: ["State", "text"], column5Header: ["Control A/B", "text"],
            column6Header: ["Control C", "text"], column7Header: ["Last Active", "text"],
            column8Header: ["Duration", "text"], column9Header: ["Room", "text"],
            column11Header: ["Custom Sort", "text"],
            // Hidden Columns
            hideColumn1: [false, "bool"], hideColumn2: [false, "bool"], hideColumn3: [false, "bool"], hideColumn4: [false, "bool"], hideColumn5: [false, "bool"], hideColumn6: [false, "bool"],
            hideColumn7: [true, "bool"], hideColumn8: [true, "bool"], hideColumn9: [true, "bool"], hideColumn10: [true, "bool"], hideColumn11: [true, "bool"], hideColumn12: [true, "bool"],
            // Info Columns
            info1Source: ["lastActive", "enum"], its1: ["80", "enum"], ita1: ["Center", "enum"], info2Source: ["lastActiveDuration", "enum"], its2: ["80", "enum"], ita2: ["Center", "enum"], info3Source: ["roomName", "enum"], its3: ["80", "enum"], ita3: ["Center", "enum"],
            // Title
            tt: ["?", "text"], ts: ["125", "enum"], tp: ["5", "enum"], ta: ["Center", "enum"],
            tc: ["#000000", "color"], tb: ["#a09fce", "color"], to: ["1", "enum"],
            // Header
            hts: ["100", "enum"], htc: ["#ffffff", "color"], hbc: ["#2375b8", "color"], hbo: ["1", "enum"],
            // Rows
            rts: ["90", "enum"], rtc: ["#000000", "color"], rbc: ["#cccccc", "color"], rbo: ["1", "enum"], sfw: ["Normal", "enum"], ratc: ["#000000", "color"], rabc: ["#cccccc", "color"], isAlternateRows: ["True", "enum"], highlightSelectedRows: ["True", "enum"], rbs: ["#FFE18F", "color"],
            // Borders
            bc: ["#000000", "color"], bwo: ["4", "enum"], bwi: ["2", "enum"], tpad: ["3", "enum"],
            // Publishing
            mySelectedRemote: ["", "text"], publishEndpoints: ["Local", "enum"], eventTimeout: ["Never", "enum"],
            // Variables
            myVariableCount: ["0", "enum"],
            // Logging
            isLogConnections: [false, "bool"], isLogActions: [true, "bool"], isLogPublish: [false, "bool"],
            isLogDeviceInfo: [false, "bool"], isLogError: [true, "bool"], isLogDebug: [false, "bool"], isLogTrace: [false, "bool"]
        ]
        firstTimeSettings.each { name, config -> app.updateSetting(name, [value: config[0].toString(), type: config[1]]) }
        
        if (state.hidden == null) state.hidden = [:]
        sensorSectionsList().each { if (state.hidden[it.replace(" ", "")] == null) state.hidden[it.replace(" ", "")] = true }
                
        // State variables for first time
        state.updatedSessionList = []
        state.compiledLocal = "<span style='font-size:32px;color:yellow'>No Devices or Not Published!</span><br>"
        state.compiledCloud = "<span style='font-size:32px;color:yellow'>No Devices or Not Published!</span><br>"
        
        applyTheme()
        state.initialized = true
    }
    
    // Post-release variable initialization — only set if null
    if (state.hidden == null) state.hidden = [Configure: false, Preview: false, Design: false]
    if (state.activeButtonA == null) state.activeButtonA = 1
    if (state.activeButtonB == null) state.activeButtonB = 21
    // Validate that customSortOrder is parseable; heal to empty list if missing or corrupt
    try {
        def testParse = new JsonSlurper().parseText(state.customSortOrder ?: "[]")
        if (!(testParse instanceof List)) throw new Exception("Not a List")
    } catch (Exception e) {
        log.warn("initialize: state.customSortOrder was corrupt ('${state.customSortOrder}') — resetting to empty. Error: $e")
        state.customSortOrder = JsonOutput.toJson([])
    }
    if (state.hidden.Preview == null) state.hidden.Preview = false
    
    // Settings that default when null — [settingName: [value, type]]
    def nullDefaults = [
        customRowCount: ["0", "enum"], isCustomSort: ["false", "enum"], isDragDrop: ["false", "bool"],
        defaultDateTimeFormat: ["0", "enum"], defaultDurationFormat: ["0", "enum"], controlSize: ["15", "enum"],
        localEndpointState: ["Enabled", "enum"], cloudEndpointState: ["Disabled", "enum"],
        crbc: ["#b6d7f1", "color"], crbc2: ["#2375b8", "color"], crtc: ["#000000", "color"], crts: ["100", "enum"],
        tilePreviewWidth: ["3", "enum"], tilePreviewHeight: ["2", "enum"],
        myRemote: ["20", "enum"], myRemoteName: ["New Remote", "text"],
        commandTimeout: ["10", "enum"], displayEndpoint: ["Local", "enum"],
        pollInterval: ["3", "enum"], pollUpdateWidth: ["3", "enum"], pollUpdateDuration: ["2", "enum"],
        shuttleHeight: ["3", "enum"], tvp: ["3", "enum"], tvpm: ["0", "enum"], tmt: ["0", "enum"],
        thp: ["5", "enum"], tpad: ["5", "enum"], hts: ["100", "enum"], hbo: ["1", "enum"],
        rts: ["90", "enum"], rbo: ["1", "enum"],
        its1: ["80", "enum"], its2: ["80", "enum"], its3: ["80", "enum"],
        ita1: ["Center", "enum"], ita2: ["Center", "enum"], ita3: ["Center", "enum"],
        temperatureDecimalPlaces: ["0 Decimal Places", "enum"], capitalizeStrings: ["False", "enum"],
        myDeviceRenameCount: ["0", "enum"], tempUnits: ["°F", "enum"],
        hideColumn11: [true, "bool"], hideColumn12: [true, "bool"],
        // New variables section
        displayCustomRow: ["All", "enum"], myVariableCount: ["0", "enum"],
        column3Width: ["200", "enum"], column4Width: ["100", "enum"],
        minBattery: ["50", "text"], minPower: ["0", "text"], minHumidity: ["50", "text"], maxHumidity: ["90", "text"], minTemp: ["50", "text"], maxTemp: ["90", "text"],
        onlyReportOutsideRangeContacts: ["False", "enum"], onlyReportOutsideRangeTemperature: ["False", "enum"], onlyReportOutsideRangeWater: ["False", "enum"],
        onlyReportOutsideRangeMotion: ["False", "enum"], onlyReportOutsideRangePresence: ["False", "enum"], onlyReportOutsideRangeSmoke: ["False", "enum"], 
        onlyReportOutsideRangeCarbonMonoxide: ["False", "enum"], onlyReportOutsideRangeHumidity: ["False", "enum"],
        onlyReportOutsideRangeBattery: ["False", "enum"], onlyReportOutsideRangePower: ["False", "enum"],
        thermDisplay: ["1", "enum"], thermTempDisplay: ["1", "enum"], sfw: ["Normal", "enum"], isAlternateRows: ["False", "enum"],
        ratc: ["#000000", "color"], rabc: ["#cccccc", "color"]
        // customSortOrder intentionally omitted — managed exclusively by the parse-validation block above
    ]
    nullDefaults.each { name, config ->
        if (settings[name] == null) app.updateSetting(name, [value: config[0].toString(), type: config[1]])
    }
    
    if (deviceStateMap == null) app.updateSetting("deviceStateMap", [value: defaultStateMap(), type: "text"])
    
    // Version-gated updates — force certain changes on existing installs when code version advances
    if (state.variablesVersion == null || state.variablesVersion < codeVersion) {
        publishSubscribe()
        state.variablesVersion = codeVersion
        
        // myVariableCount is a new setting not present in older installs
        if (settings.myVariableCount == null) app.updateSetting("myVariableCount", [value: "0", type: "enum"])
        
        //New Settings being introduced with version 6.
        if (settings.thermDisplay == null) app.updateSetting("thermDisplay", [value: "1", type: "enum"])        
        if (settings.thermTempDisplay == null) app.updateSetting("thermTempDisplay", [value: "1", type: "enum"])        
        if (settings.sfw == null) app.updateSetting("sfw", [value: "Normal", type: "enum"])        
        if (settings.isAlternateRows == null) app.updateSetting("isAlternateRows", [value: "False", type: "enum"])        
		if (settings.ratc == null) app.updateSetting("ratc", [value: "#000000", type: "color"])
		if (settings.rabc == null) app.updateSetting("rabc", [value: "#cccccc", type: "color"])
	}
    
    // Fix spelling mistake from older versions: "Seperator" -> "Separator"
    (1..customRowCount.toInteger()).each { i ->
        def rowType = settings["customRowType$i"]?.toString()
        if (rowType == "Seperator Row" || rowType == "Separator Row") { app.updateSetting("customRowType$i", [value: "Group Row", type: "enum"]) }
    }
}

def applyTheme() {
    def themes = [
        Blue:   [crbc:"#b6d7f1", crbc2:"#2375b8", crtc:"#000000", htc:"#ffffff", hbc:"#2375b8", rtc:"#000000", rbc:"#cccccc", ratc:"#000000", rabc:"#cccccc"],
        Green:  [crbc:"#c8e6c9", crbc2:"#388e3c", crtc:"#000000", htc:"#ffffff", hbc:"#388e3c", rtc:"#000000", rbc:"#d6e8d4", ratc:"#000000", rabc:"#d6e8d4"],
        Orange: [crbc:"#ffe0b2", crbc2:"#f57c00", crtc:"#000000", htc:"#ffffff", hbc:"#f57c00", rtc:"#000000", rbc:"#ffddaa", ratc:"#000000", rabc:"#ffddaa"],
        Brown:  [crbc:"#d7ccc8", crbc2:"#5d4037", crtc:"#000000", htc:"#ffffff", hbc:"#5d4037", rtc:"#000000", rbc:"#dacea4", ratc:"#000000", rabc:"#dacea4"],
        Purple: [crbc:"#e1bee7", crbc2:"#7b1fa2", crtc:"#000000", htc:"#ffffff", hbc:"#7b1fa2", rtc:"#000000", rbc:"#d9b8db", ratc:"#000000", rabc:"#d9b8db"],
        Pink:   [crbc:"#fce4ec", crbc2:"#f8bbd0", crtc:"#333333", htc:"#333333", hbc:"#f48fb1", rtc:"#333333", rbc:"#c6dee2", ratc:"#333333", rabc:"#c6dee2"],
        Mono:   [crbc:"#E0E0E0", crbc2:"#393939", crtc:"#212121", htc:"#ffffff", hbc:"#4b4949", rtc:"#000000", rbc:"#d1d1d1", ratc:"#000000", rabc:"#d1d1d1"],
        Modern: [crbc:"#e8d5b7", crbc2:"#a0784a", crtc:"#333333", htc:"#ffffff", hbc:"#a47d50", rtc:"#333333", rbc:"#fdf6ec",  ratc:"#333333", rabc:"#f9f9f9"],
        Sand:   [crbc:"#f0e6c8", crbc2:"#b8973e", crtc:"#3d2f00", htc:"#ffffff", hbc:"#c9a84c", rtc:"#3d2f00", rbc:"#fffde7",  ratc:"#3d2f00", rabc:"#fdf5c0"],
        Rose:   [crbc:"#d4a0a0", crbc2:"#9e5e5e", crtc:"#3b1f1f", htc:"#ffffff", hbc:"#b07070", rtc:"#000000", rbc:"#f5e0e0",  ratc:"#000000", rabc:"#e1bcbc"],
        Grey:   [crbc:"#d0d5db", crbc2:"#7a8694", crtc:"#1a1a1a", htc:"#ffffff", hbc:"#8e99a4", rtc:"#1a1a1a", rbc:"#f8f9fa",  ratc:"#1a1a1a", rabc:"#eceef0"]
    ]

    def t = themes[settings.theme?.toString()] ?: themes.Blue
    t.each { k, v -> app.updateSetting(k, [value: v, type: "color"]) }
    def isAdditionalSettings = settings.theme?.toString() in ["Modern", "Sand", "Rose", "Grey"]

    // Common to both styles
    def commonSettings = [crts:"100", hbo:"1", rts:"90", rbo:"1", its1:"80", its2:"80", its3:"80", ita1:"Center", ita2:"Center", ita3:"Center", highlightSelectedRows:"True"]

    // Style-specific overrides
    def specificSettings = isAdditionalSettings ? [hts:"95", bwo:"0", bwi:"1", tpad:"6", tvp:"6", thp:"8", bc:"#e0e0e0", rbs:"#e8f0fe"] : [hts:"100", bwo:"4", bwi:"2", tpad:"3", tvp:"3", thp:"5", bc:"#000000", rbs:"#DCE775"]

    (commonSettings + specificSettings).each { k, v ->
        def type = v.startsWith("#") ? "color" : "enum"
        app.updateSetting(k, [value: v, type: type])
    }
}

def updated(){
    if(!state?.isInstalled) { state?.isInstalled = true }
}
//***********************************************  End Standard System Elements  *************************************************


//******************************************  Start Device Renaming and Variable Substitution  *******************************************
// Replace %var11% - %var105% variables that may be found in device/group strings or device names with actual values.
def replaceVarsInString(str) {
    if (!str) return ""
    def dpMap = ["var": 0, "Var": 1, "vAr": 2, "vaR": 3]
    def pattern = /%([vV][aA][rR])(\d+)%/

    return str.replaceAll(pattern) { fullMatch, varLabel, varNum ->
        def dp = dpMap[varLabel]
        if (dp == null) return fullMatch // Unrecognized casing
        def i = varNum.toInteger()
        def replacement = getVariableText(i, dp)
		return replacement ?: fullMatch // fallback to original if null/empty
    }
}

// Helper Function to replace variables with their attribute values.
String getVariableText(var, dp) {
    def dev, attrIndex
    def varStr = var.toString()
    def myValue = null
    if (var < 11) {
        dev = var
        attrIndex = 0
    } else {
        attrIndex = varStr[-1] as Integer
        dev = varStr[0..-2] as Integer
    }

    // Check Device Attribute
    if (settings["variableSource${dev}"] == "Device Attribute" && settings["myDevice${dev}"] != null && settings["myAttribute${var}"] != null) {
        def rawValue = settings["myDevice${dev}"]?.currentValue(settings["myAttribute${var}"])
        myValue = (rawValue != null) ? rawValue : invalidAttribute.toString()
        
        def myStateMap = new JsonSlurper().parseText(deviceStateMap)
        def key = myValue.toString()
        def matchedKey = myStateMap?.keySet()?.find { it.equalsIgnoreCase(key) }
        if (matchedKey) { myValue = myStateMap[matchedKey] }
        
        if (isLogDebug) log.debug("getVariableText(Device - $var) - Attribute: ${settings["myAttribute${var}"]} = $myValue")
    }
    
    // Check Hub Variable
    if (settings["variableSource${dev}"] == "Hub Variable" && settings["myHubVariable${var}"] != null) {
        def myMap = getGlobalVar(settings["myHubVariable${var}"])
        //Check to see if it is a datetime format. If it is we will format it using the defaultDateTimeFormat
        if (myMap?.type == "datetime") {
            def rawValue = myMap?.value
            if (rawValue != null) {
                myValue = formatTime(rawValue, defaultDateTimeFormat.toInteger())
            } else {
                myValue = invalidAttribute.toString()
            }
        } else {
            myValue = myMap?.value?.toString() ?: invalidAttribute.toString()
        }
        if (isLogDebug) log.debug("getVariableText(Hub Variable - $var) - Hub Var: ${settings["myHubVariable${var}"]} = $myValue")
    }

    // Check Hub Property
    if (settings["variableSource${dev}"] == "Hub Property" && settings["myHubProperty${var}"] != null) {
        myValue = getHubProperty(settings["myHubProperty${var}"])
        if (isLogDebug) log.debug("getVariableText(Hub Property - $var) - Hub Property: ${settings["myHubProperty${var}"]} = $myValue")
    }

    if (myValue == null) return invalidAttribute.toString()
    
    def str = myValue.toString().trim()
    try {
        def num = new BigDecimal(str)
        def rounded = num.setScale(dp, BigDecimal.ROUND_HALF_UP)
        return rounded.toPlainString()
    } catch (e) {
        return str
    }
}

//Perform any Device Renaming requested using the Device Name Modification Fields. Fill out an %varXX% variables.
def getShortName(myDevice){
	if (!myDevice) return "Unknown"
	def shortName = myDevice
    
    //Replaces any undesireable characters in the devicename - Case Sensitive
    //Goes through each of the Device Rename Fields 1 - 12 and performs the required search and replace functions.
    for (int i = 1; i <= myDeviceRenameCount.toInteger(); i++) {
        def search = this."mySearchText$i"
        def replace = this."myReplaceText$i"
        if (replace == null || replace == "?") { this."myReplaceText$i" = ""; replace = "" }
        if (search != null && search != "?") { shortName = shortName.replace(search, replace) }
    }
    //Replaces any %varX% variables found.
    shortName = toHTML(replaceVarsInString(shortName))
    return shortName
}

//Retrieves Hub Properties such as sunrise, sunset, hsmStatus etc.
def getHubProperty(hubPropertyName) {
    if (isLogTrace) log.info("<b>getHubProperty() Received $hubPropertyName</b>")
    def sunrise = getTodaysSunrise()
    def sunset = getTodaysSunset()
    def currentTime = new Date()
    
    def timeFormats = [ sunrise: 'HH:mm a', sunrise1: 'HH:mm', sunrise2: 'h:mm a', sunset: 'HH:mm a', sunset1: 'HH:mm', sunset2: 'h:mm a', currentTime: 'HH:mm a', currentTime1: 'HH:mm', currentTime2: 'h:mm a']
    
    // Handle all time format lookups
    if (hubPropertyName.startsWith("sunrise") && timeFormats[hubPropertyName]) return sunrise.format(timeFormats[hubPropertyName])
    if (hubPropertyName.startsWith("sunset") && timeFormats[hubPropertyName]) return sunset.format(timeFormats[hubPropertyName])
    if (hubPropertyName.startsWith("currentTime") && timeFormats[hubPropertyName]) return new SimpleDateFormat(timeFormats[hubPropertyName]).format(currentTime)
    
    switch (hubPropertyName) {
        case "hubName": return location.hub
        case "currentMode": return location.properties.currentMode.toString()
        case "hsmStatus": return location.hsmStatus
        case "firmwareVersionString": return location.hub.firmwareVersionString.toString()
        case "uptime": return convertSecondsToDHMS((long) location.hub.uptime, false)
        case "timeZone": return location.timeZone.getDisplayName(false, TimeZone.SHORT)
        case "daylightSavingsTime": return location.timeZone.inDaylightTime(new Date()).toString()
        default:
            log.error("getHubProperty: $hubPropertyName was not found. Returning 'EmptyHubProperty'")
            return "EmptyHubProperty"
    }
}


//***************************************  Device --> User Group Assignement  ****************************************
//Assigns a list of Devices to one of the User created Groups
def autoAssignDevicesToGroup(groupNumber, deviceList, sensorTypeCode) {
    if (deviceList == null || deviceList.isEmpty()) {
        if (isLogDebug) log.debug "autoAssign: deviceList is null or empty for sensorTypeCode: ${sensorTypeCode}"
        return
    }
    
    def sortOrder = new groovy.json.JsonSlurper().parseText(state.customSortOrder ?: "[]")
    
    def targetID = "-${groupNumber}".toString()
    def targetUID = "${targetID}-51".toString()
    def insertAfterIndex = sortOrder.findIndexOf { it.UID?.toString() == targetUID }
    
    // If the group row doesn't exist in the sort order yet, add it at the end
    if (insertAfterIndex == -1) {
        if (isLogDebug) log.debug "autoAssign: Group row ${targetUID} not found — inserting it into sort order."
        def groupEntry = [ID: targetID, UID: targetUID, row: sortOrder.size() + 1]
        sortOrder.add(groupEntry)
        insertAfterIndex = sortOrder.size() - 1
    }
    
    def newUIDs = deviceList.collect { "${it.id}-${sensorTypeCode}".toString() } as Set
    sortOrder.removeAll { it.UID?.toString() in newUIDs }
    
    // Re-find after removal
    insertAfterIndex = sortOrder.findIndexOf { it.UID?.toString() == targetUID }
    
    def newEntries = deviceList.collect { device -> [ID: device.id.toString(), UID: "${device.id}-${sensorTypeCode}".toString()] }
    
    sortOrder.addAll(insertAfterIndex + 1, newEntries)
    sortOrder.eachWithIndex { entry, idx -> entry.row = idx + 1 }
    
    state.customSortOrder = groovy.json.JsonOutput.toJson(sortOrder)
    if (isLogDebug) log.debug "autoAssign: Assigned ${deviceList.size()} device(s) of type ${sensorTypeCode} to group ${groupNumber}. Sort order size: ${sortOrder.size()}"
}


//***************************************  Device Data Collection and Preparation Functions  ****************************************
// Gets the state of the various lights that are being tracked and puts them into a JSON format for inclusion with the script 
def getJSON() {
    if (isLogTrace) log.trace("<b>Entering: GetJSON</b>")
    def timeStart = now()
    def deviceAttributesList = []
    
    // Iterate through each device
    myDevices.each { device ->
        def deviceData = new LinkedHashMap()
        def deviceID = device.getId().toString()
        def cachedDevice = state.deviceList.find { it.ID == deviceID }
        deviceData.put("ID", device.getId())
        deviceData.put("name", getShortName(cachedDevice?.rawName ?: "Unknown"))
        def mySwitch = device.currentValue("switch")
        deviceData.put("switch", mySwitch)
        deviceType = cachedDevice?.type
        deviceData.put("type", deviceType)
        def deviceTypeHandlers = [
            1 : { d, dd -> def s = d.currentValue("switch"); def ic = getIcon(1, s); dd.icon = ic?.icon; dd.cl = ic?.class },
            2 : { d, dd -> def s = d.currentValue("switch"); def ic = getIcon(2, s); dd.level = d.currentValue("level")?.toInteger() ?: 100; dd.icon = ic?.icon; dd.cl = ic?.class },
            3 : { d, dd -> def s = d.currentValue("switch"); def ic = getIcon(3, s); dd.level = d.currentValue("level")?.toInteger() ?: 100; dd.CT = d.currentValue("colorTemperature")?.toInteger() ?: 2000; dd.icon = ic?.icon; dd.cl = ic?.class },
            4 : { d, dd -> def s = d.currentValue("switch"); def ic = getIcon(4, s); dd.level = d.currentValue("level")?.toInteger() ?: 100; dd.CT = d.currentValue("colorTemperature")?.toInteger() ?: 2000; def hsv = [hue: d.currentValue("hue") ?: 100, saturation: d.currentValue("saturation") ?: 100, value: dd.level]; dd.color = getHEXfromHSV(hsv); dd.icon = ic?.icon; dd.cl = ic?.class },
            5 : { d, dd -> def s = d.currentValue("switch"); def ic = getIcon(5, s); dd.level = d.currentValue("level")?.toInteger() ?: 100; dd.CT = d.currentValue("colorTemperature")?.toInteger() ?: 2000; def hsv = [hue: d.currentValue("hue") ?: 100, saturation: d.currentValue("saturation") ?: 100, value: dd.level]; dd.color = getHEXfromHSV(hsv); dd.colorMode = d.currentValue("colorMode"); dd.icon = ic?.icon; dd.cl = ic?.class },
            10: { d, dd -> def v = d.currentValue("valve"); def s = (v == "open") ? "on" : "off"; def ic = getIcon(10, s); dd.switch = s; dd.icon = ic?.icon; dd.cl = ic?.class },
            11: { d, dd -> def l = d.currentValue("lock"); def s = (l == "locked") ? "on" : "off"; def ic = getIcon(11, s); dd.switch = s; dd.icon = ic?.icon; dd.cl = ic?.class },
            13: { d, dd -> def dr = d.currentValue("door"); def s = (dr == "closed") ? "on" : "off"; def ic = getIcon(13, dr); dd.door = dr; dd.switch = s; dd.icon = ic?.icon; dd.cl = ic?.class },
            14: { d, dd -> def st = d.currentValue("windowShade"); def pos = d.currentValue("position"); def s = (st == "closed") ? "off" : "on"; def ic = getIcon(14, st); dd.windowShade = st; dd.position = pos; dd.switch = s; dd.icon = ic?.icon; dd.cl = ic?.class },
            15: { d, dd -> def st = d.currentValue("windowShade"); def s = (st == "closed") ? "off" : "on"; def ic = getIcon(15, st); dd.position = d.currentValue("position"); dd.tilt = Math.round(d.currentValue("tilt") * 0.9); dd.switch = s; dd.icon = ic?.icon; dd.cl = ic?.class },
            16: { d, dd -> def m = d.currentValue("mute"); def s = (m == "muted") ? "off" : "on"; def ic = getIcon(16, s); dd.switch = s; dd.volume = d.currentValue("volume"); dd.icon = ic?.icon; dd.cl = ic?.class },
            17: { d, dd -> def sp = d.currentValue("speed"); def s = (sp == "off") ? "off" : "on"; def ic = getIcon(17, sp); dd.speed = sp; dd.switch = s; dd.icon = ic?.icon; dd.cl = ic?.class; dd.speeds = 3 },
            18: { d, dd -> def sp = d.currentValue("speed"); def s = (sp == "off") ? "off" : "on"; def ic = getIcon(18, sp); dd.speed = sp; dd.switch = s; dd.icon = ic?.icon; dd.cl = ic?.class; dd.speeds = 5 },
            19: { d, dd ->
                    def m           = (d.currentValue("thermostatMode") ?: "off").toLowerCase()
                    def os          = (d.currentValue("thermostatOperatingState") ?: "idle").toLowerCase()
                    def iconState   = (os == "idle" && m == "off") ? "off" : (os == "idle" && m == "emergency heat") ? "emergency heat" : os
                    def ic          = getIcon(19, iconState)
                    def rawTemp     = d.currentValue("temperature")
                    def formattedTemp = rawTemp != null ? (tempDecimalPlaces == "0 Decimal Places" ? (rawTemp as float).round(0).toInteger().toString() + tempUnits : (rawTemp as float).round(1).toString() + tempUnits) : invalidAttribute.toString()
                    dd.thermostatMode    = m
                    dd.thermostatFanMode = d.currentValue("thermostatFanMode") ?: "auto" ; dd.heatingSetpoint = d.currentValue("heatingSetpoint") ; dd.coolingSetpoint = d.currentValue("coolingSetpoint")
                	dd.temperature = formattedTemp ; dd.operatingState = os ; dd.switch = m ; dd.icon = ic?.icon ; dd.cl = ic?.class
                	if (d.hasCapability("RelativeHumidityMeasurement")) dd.humidity = d.currentValue("humidity")
                },
        ]
        def handler = deviceTypeHandlers[deviceType]
        if (handler) { handler(device, deviceData) }
        def deviceDetails = getDeviceInfo(device, deviceData.get("type"))
        def deviceUID = "${device.getId()}-${deviceType}".toString()
        if (hideColumn7 == false) { def src = getGroupInfoSource(deviceUID, 1); deviceData.put("i1", src == "blank" ? invalidAttribute.toString() : deviceDetails."${src}") }
        if (hideColumn8 == false) { def src = getGroupInfoSource(deviceUID, 2); deviceData.put("i2", src == "blank" ? invalidAttribute.toString() : deviceDetails."${src}") }
        if (hideColumn9 == false) { def src = getGroupInfoSource(deviceUID, 3); deviceData.put("i3", src == "blank" ? invalidAttribute.toString() : deviceDetails."${src}") }
        deviceAttributesList << deviceData
    }
    def sensorConfigs = [
        31: [list: myContacts, attr: "contact", iconAttrVal: { it -> it }, condition: { val -> if (isDragDrop == true) return true; if (onlyReportOutsideRangeContacts == "False") return true; return val == "open" }],
        32: [list: myTemps, attr: "temperature", iconAttrVal: { "temp" },
                processVal: { val -> float t = val as float; if (tempDecimalPlaces == "0 Decimal Places") return t.round(0).toInteger().toString() + tempUnits;
                if (tempDecimalPlaces == "1 Decimal Place") return t.round(1).toString() + tempUnits; return t.toString() + tempUnits },
                condition: { val -> if (isDragDrop == true) return true; if (onlyReportOutsideRangeTemperature == "False") return true; float t = val as float; float tMin = minTemp.toFloat(); float tMax = maxTemp.toFloat(); return (t <= tMin || t >= tMax) }],
        33: [list: myLeaks, attr: "water", iconAttrVal: { it -> it }, condition: { val -> if (isDragDrop == true) return true; if (onlyReportOutsideRangeWater == "False") return true; return val == "wet" }],
        34: [list: myMotion, attr: "motion", iconAttrVal: { it -> it }, condition: { val -> if (isDragDrop == true) return true; if (onlyReportOutsideRangeMotion == "False") return true; return val == "active" }],
        35: [list: myPresence, attr: "presence", iconAttrVal: { it -> it }, condition: { val -> if (isDragDrop == true) return true; if (onlyReportOutsideRangePresence == "False") return true; return val == "not present" }],
        36: [list: mySmoke, attr: "smoke", iconAttrVal: { it -> it }, condition: { val -> if (isDragDrop == true) return true; if (onlyReportOutsideRangeSmoke == "False") return true; return val == "detected" }],
        37: [list: myCarbonMonoxide, attr: "carbonMonoxide", iconAttrVal: { it -> it }, condition: { val -> if (isDragDrop == true) return true; if (onlyReportOutsideRangeCarbonMonoxide == "False") return true; return val == "detected" }],
        38: [list: myHumidity, attr: "humidity", iconAttrVal: { "humidity" },
                processVal: { val -> float h = val as float; if (tempDecimalPlaces == "0 Decimal Places") return h.round(0).toInteger().toString() + "%";
                if (tempDecimalPlaces == "1 Decimal Place") return h.round(1).toString() + "%"; return h.toString() + "%" },
                condition: { val -> if (isDragDrop == true) return true; if (onlyReportOutsideRangeHumidity == "False") return true; float h = val as float; float hMin = minHumidity.toFloat(); float hMax = maxHumidity.toFloat(); return (h <= hMin || h >= hMax) }],
        39: [list: myBattery, attr: "battery", iconAttrVal: { val -> float b = val as float
                if (b > 85) return "battery_android_6"; if (b > 70) return "battery_android_5"; if (b > 55) return "battery_android_4"; if (b > 40) return "battery_android_3"; if (b > 25) return "battery_android_2"; if (b > 10) return "battery_android_1"; return "battery_android_0" },
                processVal: { val -> float b = val as float; return b.round(0).toInteger().toString() + "%" },
                condition: { val -> if (isDragDrop == true) return true; if (onlyReportOutsideRangeBattery == "False") return true; float b = val as float; return b <= minBattery.toFloat() }],
        40: [list: myPower, attr: "power", iconAttrVal: { val -> float p = val as float; return p > 0 ? "power_on" : "power_off" },
                processVal: { val -> float p = val as float; return p.round(0).toInteger().toString() + "W" },
                condition: { val -> if (isDragDrop == true) return true; if (onlyReportOutsideRangePower == "False") return true; float p = val as float; return p >= minPower.toFloat() }],
    ]
    sensorConfigs.each { type, cfg ->
        cfg.list?.each { device ->
            def deviceData = new LinkedHashMap()
            def deviceID = device.getId().toString()
            def deviceUID = "${deviceID}-${type}".toString()
            deviceData.put("ID", deviceID)
            def cachedSensor = state.deviceList.find { it.ID == deviceID && it.type == type }
            deviceData.put("name", getShortName(cachedSensor?.rawName ?: "Unknown"))
            deviceData.put("type", type)
            def rawVal = device.currentValue(cfg.attr)
            def displayVal = (rawVal != null && cfg.processVal) ? cfg.processVal(rawVal) : (rawVal ?: invalidAttribute.toString())
            deviceData.put("switch", displayVal)
            def iconInfo = rawVal != null ? getIcon(type, cfg.iconAttrVal(rawVal)) : [icon: "error", class: "warn"]
            deviceData.put("icon", iconInfo?.icon)
            deviceData.put("cl", iconInfo?.class)
            def deviceDetails = getDeviceInfo(device, type)
            if (!hideColumn7) {
                def src = getGroupInfoSource(deviceUID, 1)
                deviceData.put("i1", src == "blank" ? invalidAttribute.toString() : deviceDetails."${src}")
            }
            if (!hideColumn8) {
                def src = getGroupInfoSource(deviceUID, 2)
                deviceData.put("i2", src == "blank" ? invalidAttribute.toString() : deviceDetails."${src}")
            }
            if (!hideColumn9) {
                def src = getGroupInfoSource(deviceUID, 3)
                deviceData.put("i3", src == "blank" ? invalidAttribute.toString() : deviceDetails."${src}")
            }
            def meetsCondition = (rawVal != null && cfg.condition) ? cfg.condition(rawVal) : true
            if (meetsCondition) deviceAttributesList << deviceData
        }
    }
    // Gather the data for the custom rows and group rows
    if (customRowCount.toInteger() > 0 && isCustomSort == "true") {
        (1..customRowCount.toInteger()).each { i ->
            def rowType = settings["customRowType$i"]?.toString()
            if (rowType == "Disabled") return
            def deviceData = new LinkedHashMap()
            def deviceID = (0 - i)
            def myType
            deviceData.put("ID", "$deviceID")
            if (rowType == "Group Row") {
                myType = 51
                def ic = getIcon(myType, "groupRow")
                deviceData.put("icon", ic?.icon); deviceData.put("cl", ic?.class)
            } else if (rowType == "Device Row") {
                myType = 52
                def ic = getIcon(myType, "deviceRow")
                deviceData.put("icon", ic?.icon); deviceData.put("cl", ic?.class)
            } else if (rowType == "iFrame Row") {
                myType = 53
                def ic = getIcon(myType, "iFrameRow")
                deviceData.put("icon", ic?.icon); deviceData.put("cl", ic?.class)
            }
            deviceData.put("type", myType)
            deviceData.put("name", toHTML(replaceVarsInString(settings["myNameText$i"]?.toString())))
            if (rowType == "iFrame Row") {
                def srcUrl = settings["myStateText$i"]?.toString() ?: ""
                def myHeight = settings["myIFrameHeight$i"]
                def myURL = "[iframe src='${srcUrl}' style='height:${myHeight}px; width:100%; border:none; overflow:hidden;'][/iframe]"
                deviceData.put("switch", toHTML(replaceVarsInString(myURL)))
            } else {
                deviceData.put("switch", toHTML(replaceVarsInString(settings["myStateText$i"]?.toString())))
            }
            if (!hideColumn5) deviceData.put("level", toHTML(replaceVarsInString(settings["myControlABText$i"]?.toString())))
            if (!hideColumn6) deviceData.put("CT", toHTML(replaceVarsInString(settings["myControlCText$i"]?.toString())))
            if (!hideColumn7) deviceData.put("i1", toHTML(replaceVarsInString(settings["myInfoAText$i"]?.toString())))
            if (!hideColumn8) deviceData.put("i2", toHTML(replaceVarsInString(settings["myInfoBText$i"]?.toString())))
            if (!hideColumn9) deviceData.put("i3", toHTML(replaceVarsInString(settings["myInfoCText$i"]?.toString())))
            deviceAttributesList << deviceData
        }
    }
    // Strip nulls and empty strings from device rows only
    def cleanedList = deviceAttributesList.collect { device ->
        def type = device.type as Integer
        if (type >= 51) return device
        device.findAll { k, v -> v != null && v != "" }
    }
    // Save compact JSON
    state.JSON = JsonOutput.toJson(cleanedList)
    // Apply custom sort order if needed
    if (isCustomSort == "true") {
        def slurper2 = new JsonSlurper()
        def list1 = slurper2.parseText(state.JSON)
        def list2 = slurper2.parseText(state.customSortOrder ?: "[]")
        def uidMap = list2.collectEntries { [(it.UID?.toString()): it.row] }
        // Find any UIDs not yet in the sort order and append them
        def maxRow = list2 ? list2.max { it.row }?.row ?: 0 : 0
        def newEntries = []
        list1.each { item ->
            def uid = "${item.ID}-${item.type}".toString()
            if (!uidMap.containsKey(uid)) {
                maxRow++
                newEntries << [UID: uid, row: maxRow]
                uidMap[uid] = maxRow
                if (isLogDebug) log.debug("getJSON: Auto-appending unrecognized UID to sort order: $uid at row $maxRow")
            }
        }
        if (newEntries) {
            list2.addAll(newEntries)
            state.customSortOrder = JsonOutput.toJson(list2)
        }
        def mergedList = list1.collect { item ->
            def uid = "${item.ID}-${item.type}".toString()
            if (uidMap.containsKey(uid)) item.row = uidMap[uid]
            return item
        }
        state.JSON = JsonOutput.prettyPrint(JsonOutput.toJson(mergedList))
    }

    // Append the type-99 header entry AFTER all sorting and processing is complete so it bypasses
    // null-stripping, UID mapping, and custom sort logic — preventing it from appearing as a table row.
    def finalList = new JsonSlurper().parseText(state.JSON)
    def headerData = new LinkedHashMap()
    headerData.put("type", 99)
    //Loop through each of the 9 column headers
	(1..9).each { i -> headerData.put("hdr${i}", toHTML(replaceVarsInString(settings["column${i}Header"] ?: ""))) }
    headerData.put("hdrTitle", (tt == "?") ? "" : toHTML(replaceVarsInString(tt ?: "")))
    finalList << headerData
    state.JSON = JsonOutput.toJson(finalList)

    def timeElapsed = now() - timeStart
    if (isLogTrace) log.trace("Leaving: getJSON()" + timeElapsed / 1000 + " seconds.")
}

def getGroupInfoSource(deviceUID, infoColumn) {
    if (!isCustomSort || isCustomSort == "false") { return settings["info${infoColumn}Source"] }
    
    def slurper = new groovy.json.JsonSlurper()
    def sortOrder = slurper.parseText(state.customSortOrder ?: "[]")
    
    def deviceEntry = sortOrder.find { it.UID == deviceUID }
    if (!deviceEntry) { return settings["info${infoColumn}Source"] }
    def deviceRow = deviceEntry.row
    
    def separatorEntry = sortOrder.findAll { entry ->
        def uid = entry.UID?.toString()
        uid?.endsWith("-51") && entry.row < deviceRow
    }.max { it.row }
    
    if (!separatorEntry) { return settings["info${infoColumn}Source"] }
    
    def separatorUID = separatorEntry.UID?.toString()
    def parts = separatorUID.tokenize("-")
    def separatorIndex = parts[0].toInteger()
    
    def groupSource = settings["info${infoColumn}SourceGroup${separatorIndex}"]
	if (!groupSource || groupSource == "Default") return settings["info${infoColumn}Source"]
    if (groupSource == "None") return "blank"
    return groupSource
}

// Return the appropriate icon and class to match the type and deviceState
def getIcon(type, deviceState) {
    //Make sure all of the Thermostat returned values are lower case.
    if (type == 19) deviceState = deviceState?.toLowerCase()
	def icons = [
        1  : [on: [icon: "toggle_on", class: "on"], off: [icon: "toggle_off", class: "off"]],
        2  : [on: [icon: "lightbulb", class: "on"], off: [icon: "light_off", class: "off"]],
        3  : [on: [icon: "lightbulb", class: "on"], off: [icon: "light_off", class: "off"]],
        4  : [on: [icon: "lightbulb", class: "on"], off: [icon: "light_off", class: "off"]],
        5  : [on: [icon: "lightbulb", class: "on"], off: [icon: "light_off", class: "off"]],
        10 : [on: [icon: "water_pump", class: "on"], off: [icon: "valve", class: "off"]],
        11 : [on: [icon: "lock", class: "good"], off: [icon: "lock_open", class: "bad"]],
        // 12 is the former fan and is unused at present
        13 : [closed: [icon: "garage_door", class: "good"], open: [icon: "garage", class: "warn"], opening: [icon: "arrow_upward", class: "blinking"], closing: [icon: "arrow_downward", class: "blinking"]],
        14 : [open: [icon: "window_closed", class: "on"], closed: [icon: "roller_shades_closed", class: "off"], opening: [icon: "arrow_upward", class: "blinking"], closing: [icon: "arrow_downward", class: "blinking"], 'partially open': [icon: "roller_shades", class: "partial"]],
        15 : [open: [icon: "window_closed", class: "on"], closed: [icon: "blinds_closed", class: "off"], opening: [icon: "arrow_upward", class: "blinking"], closing: [icon: "arrow_downward", class: "blinking"], 'partially open': [icon: "blinds", class: "on"]],
        16 : [on: [icon: "volume_up", class: "on"], off: [icon: "volume_off", class: "off"]],
        17 : [on: [icon: "mode_fan", class: "on"], off: [icon: "mode_fan_off", class: "off"], low: [icon: "mode_fan", class: "spin-low"], medium: [icon: "mode_fan", class: "spin-medium"], high: [icon: "mode_fan", class: "spin-high"]],
		18 : [on: [icon: "mode_fan", class: "on"], off: [icon: "mode_fan_off", class: "off"], low: [icon: "mode_fan", class: "spin-low"], "medium-low": [icon: "mode_fan", class: "spin-medium-low"], medium: [icon: "mode_fan", class: "spin-medium"], "medium-high": [icon: "mode_fan", class: "spin-medium-high"], high: [icon: "mode_fan", class: "spin-high"]],
        19 : [cooling: [icon: "mode_cool", class: "cooling"], heating: [icon: "mode_heat", class: "heating"], 'fan only': [icon: "mode_fan", class: "spin-medium"], idle: [icon: "thermostat_auto", class: "inactive"], off: [icon: "power_off", class: "inactive"], 'pending cool': [icon: "mode_cool", class: "cooling"], 'pending heat': [icon: "mode_heat", class: "heating"], 'emergency': [icon: "emergency_heat", class: "bad"]],
        
        // Sensors start at 31
        31 : [open: [icon: "expand_content", class: "warn"], closed: [icon: "collapse_content", class: "off"]],
		32 : [temp: [icon: "device_thermostat", class: "off"]],
		33 : [wet: [icon: "water_drop", class: "bad"], dry: [icon: "format_color_reset", class: "off"] ],
        34 : [active: [icon: "detection_and_zone", class: "blinking_orange"], inactive: [icon: "zone_person_idle", class: "off"] ],
        35 : [present: [icon: "home", class: "good"], 'not present': [icon: "directions_car", class: "warn"] ],
        36 : [clear: [icon: "detector_smoke", class: "off"], detected: [icon: "detector_smoke", class: "bad"], tested: [icon: "detector_status", class: "good"] ],
        37 : [clear: [icon: "detector_co", class: "off"], detected: [icon: "detector_co", class: "bad"], tested: [icon: "detector_status", class: "good"] ],
        38 : [humidity: [icon: "humidity_percentage", class: "off"]],
        39 : [ battery_android_6 : [icon: "battery_android_6", class: "good"], battery_android_5 : [icon: "battery_android_5", class: "good"], battery_android_4 : [icon: "battery_android_4", class: "warn"], battery_android_3 : [icon: "battery_android_3", class: "warn"],
            	battery_android_2 : [icon: "battery_android_2", class: "bad"], battery_android_1 : [icon: "battery_android_1", class: "bad"], battery_android_0 : [icon: "battery_android_0", class: "bad"],],
        40 : [ power_on  : [icon: "bolt", class: "on"], power_off : [icon: "bolt", class: "off"],],
		
        //Custom Devices start at 51
        51 : [groupRow: [icon: "atr", class: "group"] ],
		52 : [deviceRow: [icon: "info", class: "off"] ],
        53 : [iFrameRow: [icon: "iframe", class: "off"] ]
    ]

    // Retrieve the entry for the given type and deviceState
    def result = icons[type]?.get(deviceState)
    
    // Return the result if found, otherwise return a default structure
	//log.info ("Returning: $result")
    return result ?: [icon: "error", class: "warn"]
}

def getDeviceInfo(device, type){
    def lastActiveEvent, lastInactiveEvent, lastActive, lastInactive, lastActiveInstant, lastInactiveInstant, lastActiveDuration, lastSeen, lastSeenElapsed
    def roomName, colorName, colorMode, power, healthStatus, energy, network, colorTemperature, deviceTypeName, lastActivity, battery, temperature, humidity
    def ID = device?.getId()
        
    // Resolve all active info sources once
    def activeSources = [info1Source, info2Source, info3Source] as Set
    if (isCustomSort == "true") {
        (1..customRowCount.toInteger()).each { i ->
            if (settings["customRowType${i}"] == "Group Row") {
                ["info1SourceGroup${i}", "info2SourceGroup${i}", "info3SourceGroup${i}"].each { key ->
                    def val = settings[key]
                    if (val && val != "Default" && val != "None") activeSources << val
                }
            }
        }
    }
    def hasSource = { String s -> activeSources.contains(s) }
    if (hasSource("roomName")) roomName = device?.getRoomName()
    if (hasSource("colorName")) colorName = device?.currentValue("colorName")
    if (hasSource("colorMode")) colorMode = device?.currentValue("colorMode")
    if (hasSource("power")) power = device?.currentValue("power")
    if (hasSource("healthStatus")) healthStatus = device?.currentValue("healthStatus")
    if (hasSource("energy")) energy = device?.currentValue("energy")
    if (hasSource("network")) network = getNetworkType(device?.getDeviceNetworkId())
    if (hasSource("deviceTypeName")) deviceTypeName = getDeviceTypeInfo(type)
    if (hasSource("battery") && device.hasCapability("Battery")) battery = device?.currentValue("battery") + "%"
    if (hasSource("colorTemperature") && device.hasCapability("ColorTemperature")) colorTemperature = device?.currentValue("colorTemperature") + "°K"
    if (hasSource("temperature") && device.hasCapability("TemperatureMeasurement")) {
        def myTemp = device?.currentValue("temperature")
        temperature = Math.round(myTemp).toInteger().toString() + tempUnits
    }
    if (hasSource("humidity") && device.hasCapability("RelativeHumidityMeasurement")) {
        def myHumid = device?.currentValue("humidity")
        humidity = Math.round(myHumid).toInteger().toString() + "%"
    }
    if (hasSource("lastActive") || hasSource("lastActiveDuration") || hasSource("lastInactive") || hasSource("lastInactiveDuration")) {
        def ec = [ 1:["switch","on","off"], 2:["switch","on","off"], 3:["switch","on","off"], 4:["switch","on","off"], 5:["switch","on","off"], 10:["valve","open","closed"], 11:["lock","locked","unlocked"], 13:["door","!closed","closed"], 
					14:["windowShade","open","!open"], 15:["windowShade","open","!open"], 17:["speed","!off","off"], 18:["speed","!off","off"], 19:["thermostatOperatingState","heating|cooling","!(heating|cooling)"], 31:["contact","open","closed"], 33:["water","wet","dry"], 
					34:["motion","active","inactive"], 35:["presence","not present","present"], 36:["smoke","!clear","clear"], 37:["carbonMonoxide","!clear","clear"] ]
        def cfg = ec[type]
        if (cfg) {
            def events = device.events(max: 500)
            def findEvent = { val ->
                def groupNegate = val.startsWith("!(") && val.endsWith(")")
                def conditions = groupNegate ? val[2..-2].split("\\|") : val.split("\\|")
                events.findAll { ev ->
                    def matches = conditions.any { cond ->
                        def negate = cond.startsWith("!")
                        def match = negate ? cond.substring(1) : cond
                        ev.name == cfg[0] && (negate ? ev?.value != match : ev?.value == match)
                    }
                    groupNegate ? !matches : matches
                }.sort { -it.date.time }?.with { it.isEmpty() ? null : it.first() }
            }
            lastActiveEvent = findEvent(cfg[1])
            lastInactiveEvent = findEvent(cfg[2])
        }
        if (lastActiveEvent != null) {
            lastActive = formatTime(lastActiveEvent?.getDate(), defaultDateTimeFormat.toInteger() ?: 3)
            lastActiveDuration = formatTime(lastActiveEvent?.getDate(), defaultDurationFormat.toInteger() ?: 21)
            lastActiveInstant = formatTime(lastActiveEvent?.getDate(), 0)
        }
        if (lastInactiveEvent != null) {
            lastInactive = formatTime(lastInactiveEvent?.getDate(), defaultDateTimeFormat.toInteger() ?: 3)
            lastInactiveDuration = formatTime(lastInactiveEvent?.getDate(), defaultDurationFormat.toInteger() ?: 21)
            lastInactiveInstant = formatTime(lastInactiveEvent?.getDate(), 0)
        }
        if (lastInactive != null && lastActive != null) {
            def durations = getDuration(lastActiveInstant, lastInactiveInstant)
            lastActiveDuration = durations.lastActiveDuration
            lastInactiveDuration = durations.lastInactiveDuration
        }
    }
    if (hasSource("lastSeen") || hasSource("lastSeenElapsed")) {
        lastActivity = device?.getLastActivity()
        if (lastActivity != null) {
            def timestamp = lastActivity.time
            lastSeen = formatTime(timestamp, defaultDateTimeFormat.toInteger() ?: 3)
            def durations = getDuration(timestamp, now())
            lastSeenElapsed = durations.lastActiveDuration
        }
    }
    
    return [lastActive: lastActive, lastInactive: lastInactive, lastInactiveInstant: lastInactiveInstant, lastActiveInstant: lastActiveInstant, lastActiveDuration: lastActiveDuration, lastInactiveDuration: lastInactiveDuration, roomName: roomName, 
            colorName: colorName, colorMode: colorMode, power: power, healthStatus: healthStatus, energy: energy, ID: ID, network: network, deviceTypeName: deviceTypeName, lastSeen: lastSeen, lastSeenElapsed: lastSeenElapsed, battery: battery, 
            temperature: temperature, humidity: humidity, colorTemperature: colorTemperature].collectEntries { key, value -> [key, value != null ? value : invalidAttribute.toString()] }
}

// Function to determine network type based on DNI length
def getNetworkType(dni) {
    def networkTypes = [2: "Z-Wave", 4: "Zigbee", 8: "LAN", 16: "Matter", 36: "Virtual"]
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

    // Define a map of actions Note: 'speed' handles all fan types (17=3-speed, 18=5-speed) generically via setSpeed()
    def commandMap = [ 'switch' : { ID, type, newValue -> handleSwitch(ID, type, newValue) }, 'level' : { ID, _, newValue -> ID.setLevel(newValue, 0.4) }, 'volume' : { ID, _, newValue -> ID.setVolume(newValue) }, 'position': { ID, _, newValue -> ID.setPosition(newValue) }, 
		'tilt' : { ID, _, newValue -> ID.setTiltLevel(Math.round(newValue * (100 / 90))) }, 'speed' : { ID, _, newValue -> ID.setSpeed(newValue) }, 'name'    : { ID, _, newValue -> ID.setLabel(newValue) }, 'CT' : { ID, _, newValue -> ID.setColorTemperature(newValue, null, 0.2) }, 
		'color' : { ID, _, newValue -> setDeviceColor(ID, newValue) }, 'thermostatMode'  : { ID, _, newValue -> ID.setThermostatMode(newValue) }, 'heatingSetpoint' : { ID, _, newValue -> ID.setHeatingSetpoint(newValue) }, 
		'coolingSetpoint' : { ID, _, newValue -> ID.setCoolingSetpoint(newValue) }, 'thermostatFanMode': { ID, _, newValue -> ID.setThermostatFanMode(newValue) }
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
    def on = (newValue == "on")
    switch(type) {
        case [1,2,3,4,5]: on ? device.on() : device.off(); break
        case 10: case [14,15]: on ? device.open() : device.close(); break
        case 11: on ? device.lock() : device.unlock(); break
        case 13: on ? device.close() : device.open(); break
        case 16: on ? device.unmute() : device.mute(); break
        case [17,18]: on ? device.setSpeed("on") : device.setSpeed("off"); break
		case 19: on ? device.setThermostatMode("auto") : device.setThermostatMode("off"); break
    }
}

// Converts a hex color to HSV and sets it for the device
def setDeviceColor(device, hexColor) {
    def RGB = hubitat.helper.ColorUtils.hexToRGB("$hexColor")
    def HSV = hubitat.helper.ColorUtils.rgbToHSV(RGB)
    device.setColor([hue: HSV[0], saturation: HSV[1], level: HSV[2]])
}

// Find the device that matches the ID.
def findDeviceById(ID) {
    return myDevices?.find { it.getId() == ID }
}


//***************************************************  Time and Date Related Functions  *************************************************
//This is derived from the formatTime function used in Grid but in this case the only Time format received is from Events which are always "java.sql.Timestamp" or from lastActivity which are of type Long.
//Receives a time as an event timestamp and converts it into one of many alternate time formats.
def formatTime(timeValue, int format) {
    if (timeValue == "N/A") return 0
    
    // Handle ISO 8601 string format (e.g. "1960-12-11T08:30:00.000-0600")
    if (getObjectClassName(timeValue) == "java.lang.String") {
        try {
            def isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            timeValue = isoFormat.parse(timeValue).getTime()
        } catch (Exception e1) {
            try {
                // Fallback: try without milliseconds
                def isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                timeValue = isoFormat.parse(timeValue).getTime()
            } catch (Exception e2) {
                return "N/A"
            }
        }
    }
    
    def myLongTime = (getObjectClassName(timeValue) == "java.sql.Timestamp") ? timeValue.getTime() : timeValue
    if (new Date(myLongTime) == null) return "N/A"
    if (format == 0) return myLongTime
    if (format in [21, 22]) { def diff = ((now() - myLongTime) / 1000).toLong(); return convertSecondsToDHMS(diff, format == 21) }
    if (format in [23, 24]) { def diff = ((myLongTime - now()) / 1000).toLong(); return convertSecondsToDHMS(diff, format == 23) }
    def targetFormat = (format == 1) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") : new SimpleDateFormat(getDateTimeFormatDescription(format))
    try { return targetFormat.format(new Date(myLongTime)) }
    catch (Exception ignored) { return timeValue }
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
    def secs = (defaultDurationFormat.toInteger() == 21)
    long diff = lastActiveEvent - lastInactiveEvent
    if (diff == 0) return [lastActiveDuration: invalidAttribute.toString(), lastInactiveDuration: invalidAttribute.toString()]
    if (diff > 0) {
        long runTime = (now() - lastActiveEvent) / 1000
        lastActiveDuration = "<span style='color: green;'>" + convertSecondsToDHMS(runTime, secs).toString() + "</span>"
        long offTime = (lastActiveEvent - lastInactiveEvent) / 1000
        lastInactiveDuration = convertSecondsToDHMS(offTime, secs).toString()
    } else {
        long offTime = (now() - lastInactiveEvent) / 1000
        lastInactiveDuration = "<span style='color: red;'>" + convertSecondsToDHMS(offTime, secs).toString() + "</span>"
        long runTime = (lastInactiveEvent - lastActiveEvent) / 1000
        lastActiveDuration = convertSecondsToDHMS(runTime, secs).toString()
    }
    return [lastActiveDuration: lastActiveDuration, lastInactiveDuration: lastInactiveDuration]
}
//******************************************  End of Time and Date Related Functions  ***********************************************


//***************************************************  Compile Time Functions  ******************************************************
//Compress the fixed components text output and generate the version that will be used by the browser.
def compile(){
	//Assign the device types
	try{
		if (isLogTrace) log.trace("<b>Entering: Compile</b>")
		if (isLogDebug) log.debug("Running Compile")
        
        //Updated the device cache info with any changes. This is non-volatile info like device name, room, deviceID etc.
		cacheDeviceInfo()
			
        //Get the HTML template
        def html1 = parent.state.smartGridTemplate
        
        def content = condense(html1)
        if (isLogDebug) log.debug("After condense size: ${content.size()}")
		
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
                
        //Column Headers - wrapped in ID'd spans so the header refresh script can live-update embedded variables.
        content = content.replace('#column1Header#', "<span id='hdr1'>" + toHTML(replaceVarsInString(column1Header)) + "</span>" )
        content = content.replace('#column2Header#', "<span id='hdr2'>" + toHTML(replaceVarsInString(column2Header)) + "</span>" )
        content = content.replace('#column3Header#', "<span id='hdr3'>" + toHTML(replaceVarsInString(column3Header)) + "</span>" )
		content = content.replace('#column4Header#', "<span id='hdr4'>" + toHTML(replaceVarsInString(column4Header)) + "</span>" )
		content = content.replace('#column5Header#', "<span id='hdr5'>" + toHTML(replaceVarsInString(column5Header)) + "</span>" )
		content = content.replace('#column6Header#', "<span id='hdr6'>" + toHTML(replaceVarsInString(column6Header)) + "</span>" )
        
		//Forced Column Widths - If the columns are marked as hidden change the width to zero.
        [3, 4, 5, 6].each { i -> content = content.replace("#column${i}Width#", settings["hideColumn${i}"] ? '0' : settings["column${i}Width"].toString()) }
        	
		//Custom Rows
		content = content.replace('#crbc#', crbc )
		content = content.replace('#crbc2#', crbc2 )
		content = content.replace('#crtc#', crtc )
        content = content.replace('#crts#', (crts.toFloat()/100).toString() )	// Custom Row Text Size - We use REM for this to break the inheritance from the tr - table row.
		
		//Info Columns
		content = content.replace('#Info1#', "<span id='hdr7'>" + toHTML(replaceVarsInString(column7Header)) + "</span>" )	// Info 1 Header Text
		content = content.replace('#its1#', its1 )	// Info 1 Text Size
		content = content.replace('#ita1#', ita1 )	// Info 1 Text Alignment
		content = content.replace('#Info2#', "<span id='hdr8'>" + toHTML(replaceVarsInString(column8Header)) + "</span>" )	// Info 2 Header Text
		content = content.replace('#its2#', its2 )	// Info 2 Text Size
		content = content.replace('#ita2#', ita2 )	// Info 2 Text Alignment
		content = content.replace('#Info3#', "<span id='hdr9'>" + toHTML(replaceVarsInString(column9Header)) + "</span>" ) 	// Info 3 Header Text
		content = content.replace('#its3#', its3 )	// Info 3 Text Size
		content = content.replace('#ita3#', ita3 )	// Info 3 Text Alignment
		if (tt == "?") { content = content.replace('#titleDisplay#', 'none' ) }
		else {
			content = content.replace('#titleDisplay#', 'block' )	// Display Title or not
			content = content.replace('#tt#', "<span id='hdrTitle'>" + toHTML(replaceVarsInString(tt)) + "</span>" )	// Title Text
			content = content.replace('#ts#', ts )	// Title Size
			content = content.replace('#tp#', tp )	// Title Padding
			content = content.replace('#ta#', ta )	// Title Alignment
			content = content.replace('#tc#', tc )	// Title Color
			def mytb = convertToHex8(tb, to.toFloat())  //Calculate the new color including the opacity.
			content = content.replace('#tb#', mytb )	// Title Background Color// Display Title or not
		}
        content = content.replace('#hts#', (hts.toFloat()/100).toString() )	// Header Text Size - We use REM for this to break the inheritance from the tr - table row.
		content = content.replace('#htc#', htc )	// Header Text Color
		content = content.replace('#sortHeaderHintAZ#', sortHeaderHintAZ )
		content = content.replace('#sortHeaderHintZA#', sortHeaderHintZA )
        
        //Configure Min Max colors for temps and humidity.
        [maxTemp: maxTemp ?: "90", minTemp: minTemp ?: "50", maxTempColor: maxTempColor ?: "#FF4500", minTempColor: minTempColor ?: "#5BC8F5", normalTempColor: normalTempColor ?: "#81C784", maxHumidity: maxHumidity ?: "90", 
			minHumidity: minHumidity ?: "50", maxHumidityColor: maxHumidityColor ?: "#5B8DB8", minHumidityColor: minHumidityColor ?: "#D4B483", normalHumidityColor: normalHumidityColor ?: "#66BB6A"].each { k, v -> content = content.replace("#${k}#", v) }
        
		def myhbc = convertToHex8(hbc, hbo.toFloat())  //Calculate the new color including the opacity.
		content = content.replace('#hbc#', myhbc )	// Header Background Color
		content = content.replace('#rts#', rts )	// Row Text Size
        content = content.replace('#sfw#', sfw )	// State Font Weight
		content = content.replace('#rtc#', rtc )	// Row Text Color
		def myrbc = convertToHex8(rbc, rbo.toFloat())  //Calculate the new color including the opacity.
		content = content.replace('#rbc#', myrbc )	// Row Background Color
		if ( highlightSelectedRows == "True" ) content = content.replace('#rbs#', rbs ) // Row Background Color Selected
		else content = content.replace('#rbs#', myrbc ) //Otherwise just set the background color to the default Row Background Color
        
        //Configure Alternate Row Colors when selected.
        if (isAlternateRows == "True" ) content = content.replace('#AlternateRowHTML#', "tr:nth-child(even):not(.custom-row-group) { background-color: " + rabc + "; color: " + ratc + " }" )
        	
		//Hide unwanted columns
		(1..12).each { i -> content = content.replace("#hideColumn${i}#", settings["hideColumn${i}"] ? 'none' : 'table-cell') }
		content = content.replace('#BrowserTitle#', myRemoteName)
		content = content.replace('#pollInterval#', (pollInterval.toInteger() * 1000).toString() )

        //Polling Settings
        ['pollUpdateColorSuccess', 'pollUpdateColorFail', 'pollUpdateColorPending', 'shuttleColor', 'shuttleHeight'].each { k -> content = content.replace("#${k}#", settings[k].toString()) }
		content = content.replace('#pollUpdateDuration#', (pollUpdateDuration.toInteger() * 1000).toString() )
		content = content.replace('#commandTimeout#', (commandTimeout.toInteger() * 1000).toString() )
		if (isPolling == "Enabled") content = content.replace('#isPolling#', "true")
		if (isPolling == "Disabled") content = content.replace('#isPolling#', "false")
	
		//Drag & Drop - Custom Sort
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
        
        //Thermostat and Temperature Preferences
        content = content.replace('#thermDisplay#', thermDisplay ?: "1")
        content = content.replace("#thermTempDisplay#", thermTempDisplay ?: "1")
        content = content.replace("#tempUnits#", tempUnits ?: "°F")
		content = content.replace("#invalidAttribute#", invalidAttribute.toString() ?: "N/A")
            
    	//Mark tags
		['markTag', 'm1Tag', 'm2Tag', 'm3Tag', 'm4Tag', 'm5Tag', 'u1Tag', 'u2Tag'].each { tag -> content = content.replace("#${tag}#", settings[tag].toString()) }
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
	if (isLogDebug) log.debug ("Condense: After all comments with leading \\\\ are removed: " + input.size() + " bytes." )
    
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
    
    // Capability-to-type mapping, ordered so higher-priority types overwrite lower ones. Type 12 is intentionally unassigned. Fans are detected later as type 17 (3-speed) or 18 (5-speed).
    def capMap = [
        [["Switch"], 1], [["SwitchLevel"], 2], [["ColorTemperature"], 3], [["ColorControl"], 4], [["ColorControl", "ColorTemperature"], 5],
        [["Valve"], 10], [["Lock"], 11], [["GarageDoorControl"], 13], [["DoorControl"], 13],
        [["WindowShade"], 14], [["WindowBlind"], 15], [["AudioVolume"], 16], [["Thermostat"], 19]
    ]
	
    // Controls
    myDevices.each { device ->
        def deviceInfo = new LinkedHashMap()
        deviceInfo.put("ID", device.getId())
        def label = (device.getLabel() ?: device.getName()) ?: "Unknown"
        deviceInfo.put("rawName", label)
        deviceInfo.put("name", getShortName(label))
        
        // FanControl is handled separately to distinguish 3-speed (17) from 5-speed (18). Type 12 is unassigned. Presence of "medium-low" in supported speeds indicates 5-speed.
        if (device.hasCapability("FanControl")) {
            def is5Speed = false
            try {
                def speedsJson = device.currentValue("supportedFanSpeeds")
                if (speedsJson) {
                    def speeds = new groovy.json.JsonSlurper().parseText(speedsJson)
                    is5Speed = speeds.contains("medium-low")
                }
            } catch (Exception e) {
                if (isLogDebug) log.debug("cacheDeviceInfo: Could not determine fan speeds for '${label}' — defaulting to 3-speed")
            }
            if (isLogDebug) log.debug("cacheDeviceInfo: Fan '${label}' is ${is5Speed ? '5-speed' : '3-speed'}")
            deviceInfo.put("type", is5Speed ? 18 : 17)
        } else {
            capMap.each { caps, typeVal ->
                if (caps.every { device.hasCapability(it) }) deviceInfo.put("type", typeVal)
            }
        }
        myDeviceList << deviceInfo
    }
    
    // Sensors
    def mySensorMap = [myContacts: 31, myTemps: 32, myLeaks: 33, myMotion: 34, myPresence: 35, mySmoke: 36, myCarbonMonoxide: 37, myHumidity: 38, myBattery: 39, myPower: 40]
    mySensorMap.each { sensorKey, type ->
        this."$sensorKey"?.each { device ->
            def deviceInfo = new LinkedHashMap()
            deviceInfo.put("ID", device.getId())
            deviceInfo.put("rawName", device.getLabel() ?: device.getName())
            deviceInfo.put("type", type)
            myDeviceList << deviceInfo
        }
    }
    
    // Determine duplicates and append suffix to raw name before renaming
    def multiTypeSensorTypes = [36, 37] as Set
    def rawNameCounts = myDeviceList.findAll { it.type in multiTypeSensorTypes }.countBy { it.rawName }
    
    // Build the set of type codes that should have their sensor type name appended
    def appendTypes = (appendTypeSensorSections ?: []).collect { sensorSectionToTypeMap()[it] }.findAll { it != null } as Set
    def maps = createDeviceTypeMap()
   
    myDeviceList.each { deviceInfo ->
        // Controls already had getShortName() applied — skip them
        if (!deviceInfo.containsKey("rawName")) return
        def baseName = deviceInfo.rawName
        if (deviceInfo.type in appendTypes) {
            def resolvedLabel = "${baseName} (${maps.typeMap[deviceInfo.type]})"
            deviceInfo.put("rawName", resolvedLabel)
            deviceInfo.name = getShortName(resolvedLabel)
        } else {
            deviceInfo.name = getShortName(baseName)
        }
	}
    state.deviceList = myDeviceList
}
//***************************************************  End of Compile Functions  ***************************************************


//***************************************************  Endpoint Activity Handling  *************************************************
//This delivers the applet content.
def showApplet() {
	if (isLogTrace) log.trace("<b>Entering: showApplet</b>")
    def isLocal = false
    def isCloud = false
    
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
		return
	}
	
	//If the app is reporting a device state change if will now be processed. Map the second group by 'ID' for easier comparison
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
        if (change.type == 19 && change.changes.containsKey("thermostatMode")) {
            def device = findDeviceById(change.ID)
            if (device) {
                //Always send the setPoint even if it has not changed because this is evaluated by the thermostat to determine the mode if Auto is selected.
                def heatSP = device.currentValue("heatingSetpoint")
                def coolSP = device.currentValue("coolingSetpoint")
                if (heatSP != null) change.changes["heatingSetpoint"] = [heatSP, heatSP]
                if (coolSP != null) change.changes["coolingSetpoint"] = [coolSP, coolSP]
            }
        }
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
//***********************************************  End of Endpoint Activity Handling  ****************************************************


//******************************************  Screen UI and Management Functions  ********************************************************
//This is the standard button handler that receives the click of any button control.
def appButtonHandler(btn) {
	if (isLogTrace) log.trace("<b>Entering: appButtonHandler: Clicked on button: $btn</b>")
	def buttonNumber
	def buttonMap = ['Controls':1, 'Sensors':2, 'RenameDevices':3, 'Inactivity':4, 'Endpoints':5, 'Polling':6, 'Variables':7, 'GroupAndSort':8, 'Publish':9, 'Logging':10, 'General': 21, 'Appearance': 22, 'Title': 23, 'Columns':  24, 'Padding': 25, 'Advanced': 26, 'Experimental': 27]
	
	try {
		buttonNumber = buttonMap[btn]
		//if ( buttonNumber != null ) log.info ("buttonNumber is: $buttonNumber")
	}
	catch (Exception ignored) { }
	
	if (buttonNumber in 1..10)      { state.activeButtonA = buttonNumber; return }
	if (buttonNumber in 20..30)     { state.activeButtonB = buttonNumber; return }
	    
	switch (btn) {
		case 'EnableDragDrop': app.updateSetting("isDragDrop", true); compile() ; break
		case 'saveCustomSort': app.updateSetting("isDragDrop", false); compile(); break
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
		case "Compile": compile(); break
		case "applyTheme": applyTheme(); break
        case "btnHideConfigure": state.hidden.Configure = state.hidden.Configure ? false : true; break
        case "btnHideDesign": state.hidden.Design = state.hidden.Design ? false : true; break
		case "btnHidePreview": state.hidden.Preview = state.hidden.Preview ? false : true; break
		case "publishSubscribe": publishSubscribe(); break
        case "unsubscribe": deleteSubscription(); break
        case "btnCollapseAllSensors": collapseAllSensors(true); break
        case "btnExpandAllSensors": collapseAllSensors(false); break
        case "btnHideTemperature":  state.hidden.Temperature  = !state.hidden?.Temperature;  break
		case "btnHideBattery":      state.hidden.Battery      = !state.hidden?.Battery;      break
        case "btnHideContacts":     state.hidden.Contacts     = !state.hidden?.Contacts;     break
        case "btnHidePresence":     state.hidden.Presence     = !state.hidden?.Presence;     break
        case "btnHideWater":        state.hidden.Water        = !state.hidden?.Water;        break
        case "btnHideSmoke":        state.hidden.Smoke        = !state.hidden?.Smoke;        break
        case "btnHideCarbonMonoxide": state.hidden.CarbonMonoxide = !state.hidden?.CarbonMonoxide; break
        case "btnHideMotion":       state.hidden.Motion       = !state.hidden?.Motion;       break
        case "btnHideHumidity":     state.hidden.Humidity     = !state.hidden?.Humidity;     break
        case "btnHidePower":        state.hidden.Power        = !state.hidden?.Power;        break
    }
}

//Collapse or expand the visibility of the sensor sections
def collapseAllSensors(collapsed) {
    sensorSectionsList().each { state.hidden[it.replace(" ", "")] = collapsed }
}

//Returns a formatted title for a section header based on whether the section is visible or not.
def getSectionTitle(section) {
    def arrow = state.hidden[section] ? "&#9654;" : "&#9660;"
    return sectionTitle("${section} ${arrow}")
}
//**************************************************  End Screen UI and Management Functions  **************************************************



//**********************************************************  Publishing Functions  ************************************************************
//This function removes all existing subscriptions for this app and replaces them with new ones corresponding to the devices and attributes being monitored.
void publishSubscribe() {
    if (isLogTrace) log.trace("<b>Entering: publishSubscribe</b>")
    if (isLogPublish) log.info("<b>Creating subscriptions for Tile: $myRemote with description: $myRemoteName.</b>")
    
    //Remove all existing subscriptions
    unsubscribe()
    
    // List of attributes you want to subscribe to
    def attributesToSubscribe = ["switch", "hue", "saturation", "level", "colorTemperature", "valve", "lock", "speed", "door", "windowShade", "position", "tilt", "mute", "volume", "contact", "water", "motion", 
                                 "presence", "smoke", "carbonMonoxide", "battery", "power", "thermostatMode", "thermostatFanMode", "heatingSetpoint", "coolingSetpoint", "thermostatOperatingState", "temperature"]
    deleteSubscription()
    
    // Configure subscriptions to devices
    [myDevices, myContacts, myTemps, myLeaks, myMotion, myPresence, mySmoke, myCarbonMonoxide, myHumidity, myBattery, myPower].each { deviceList ->
        deviceList?.each { device ->
            attributesToSubscribe.each { attribute ->
                if (device.hasAttribute(attribute)) subscribe(device, attribute, handler)
            }
        }
    }
    
    // Configure subscriptions to variables
    for (int i = 1; i <= myVariableCount.toInteger(); i++) {
        if (settings["variableSource${i}"] == "Device Attribute") {
            def varDevice = settings["myDevice${i}"]
            if (varDevice == null) continue
            (1..5).each { j ->
                def attrIndex = i * 10 + j
                def varAttr = settings["myAttribute${attrIndex}"]
                if (varAttr && varDevice.hasAttribute(varAttr)) {
                    subscribe(varDevice, varAttr, handler)
                    if (isLogDebug) log.debug("Subscribed variable ${i} slot ${j}: device=${varDevice}, attr=${varAttr}")
                }
            }
        }
        if (settings["variableSource${i}"] == "Hub Variable") {
            (1..5).each { j ->
                def attrIndex = i * 10 + j
                def varName = settings["myHubVariable${attrIndex}"]
                if (varName) {
                    subscribe(location, "variable:${varName}", "handler")
                    if (isLogDebug) log.debug("Subscribed hub variable ${i} slot ${j}: ${varName}")
                }
            }
        }
    }
    
    //Ensure any changes are incorporated in the compiled version.
    compile()
    
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
    if (isLogPublish) log.info("deleteSubscription: Deleted all subscriptions. To verify click on the App ?? Symbol and look for the Event Subscriptions section.")
    unsubscribe()
}

//*******************************************************  End of Publishing Functions  *********************************************************


//*******************************************************************  Utility Functions  ***************************************************************
//Returns a string containing the var if it is not null. Used for the controls.
static String bold2(s, var) {
    if (var == null) return "<b>$s (N/A)</b>"
    else return ("<b>$s ($var)</b>")
}

//Functions to enhance text appearance
static String bold(s) { return "<b>$s</b>" }

//Set the Section Titles to a consistent style.
static def sectionTitle(title) { return "<span style='color:#000; margin-top:1em; font-size:16px; box-shadow: 0px 0px 3px 3px #40b9f2; padding:1px; background:#40b9f2;'><b>${title}</b></span>" }

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

//Convert (HTML) tags to <HTML> for display.
def toHTML(HTML){    
    if (HTML == null) return ""
    myHTML = HTML.replace("[", "<")
    myHTML = myHTML.replace("]", ">")
    return myHTML
}

//*******************************************************************  End Utility Functions  ***********************************************************************************************
