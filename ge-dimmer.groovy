/**
  *  Modifications Copyright 2017 Corey Wendling
  *
  *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
  *  in compliance with the License. You may obtain a copy of the License at:
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
  *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
  *  for the specific language governing permissions and limitations under the License.
  *
  **/
 
/**
  *  Copyright 2015 SmartThings
  *
  *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
  *  in compliance with the License. You may obtain a copy of the License at:
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
  *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
  *  for the specific language governing permissions and limitations under the License.
  *
  **/
 
metadata {
  definition (name: "Dimmer Switch", namespace: "astrobass", author: "Astro Bass", ocfDeviceType: "oic.d.light") {
    capability "Switch Level"
    capability "Actuator"
    capability "Indicator"
    capability "Switch"
    capability "Polling"
    capability "Refresh"
    capability "Sensor"
    capability "Health Check"
    capability "Light"
    fingerprint mfr:"0063", prod:"4457", deviceJoinName: "GE In-Wall Smart Dimmer"
    fingerprint mfr:"0063", prod:"4944", deviceJoinName: "GE In-Wall Smart Dimmer"
    fingerprint mfr:"0063", prod:"5044", deviceJoinName: "GE Plug-In Smart Dimmer"
    fingerprint mfr:"0063", prod:"4944", model:"3034", deviceJoinName: "GE In-Wall Smart Fan Control"
    command "ignoreStartLevelOff"
    command "ignoreStartLevelOn"
    command "setSteps"
    command "setDelay"
  }

  simulator {
    status "on":  "command: 2003, payload: FF"
    status "off": "command: 2003, payload: 00"
    status "09%": "command: 2003, payload: 09"
    status "10%": "command: 2003, payload: 0A"
    status "33%": "command: 2003, payload: 21"
    status "66%": "command: 2003, payload: 42"
    status "99%": "command: 2003, payload: 63"
    reply "2001FF,delay 5000,2602": "command: 2603, payload: FF"
    reply "200100,delay 5000,2602": "command: 2603, payload: 00"
    reply "200119,delay 5000,2602": "command: 2603, payload: 19"
    reply "200132,delay 5000,2602": "command: 2603, payload: 32"
    reply "20014B,delay 5000,2602": "command: 2603, payload: 4B"
    reply "200163,delay 5000,2602": "command: 2603, payload: 63"
  }

  tiles {
    multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
      tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
        attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00a0dc", nextState:"turningOff"
        attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
        attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00a0dc", nextState:"turningOff"
        attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
      }
      tileAttribute ("device.level", key: "SLIDER_CONTROL") {
        attributeState "level", action:"switch level.setLevel"
      }
    }
    standardTile("indicator", "device.indicatorStatus", width: 2, height: 2, decoration: "flat") {
      state "when off", action:"indicator.indicatorWhenOn", icon:"st.indicators.lit-when-off"
      state "when on", action:"indicator.indicatorNever", icon:"st.indicators.lit-when-on"
      state "never", action:"indicator.indicatorWhenOff", icon:"st.indicators.never-lit"
    }
    valueTile("level", "device.level", decoration: "flat", width: 2, height: 2) {
      state "level", label:'${currentValue} %', unit:"%", backgroundColor:"#ffffff"
    }
    standardTile("refresh", "device.switch", width: 2, height: 2, decoration: "flat") {
      state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
    }
    standardTile("ignoreStartLevel", "device.ignoreStartLevel", width: 2, height: 2, decoration: "flat") {
      state "off", label:'${name}', action:"ignoreStartLevelOn", backgroundColor:"#FF0000"
      state "on", label:'${name}', action:"ignoreStartLevelOff", backgroundColor:"#00FF00"
    }
    controlTile("steps", "device.steps", "slider", width: 2, height: 2, range:"(1..99)") {
      state "level", label:'Manual Steps', action: "setSteps"
    }
    controlTile("delay", "device.delay", "slider", width: 2, height: 2, range:"(1..255)") {
      state "level", label:'Manual Delay', action: "setDelay"
    }
    main(["switch"])
    details(["switch", "indicator", "level", "refresh", "ignoreStartLevel", "steps", "delay"])
  }
}

def parse(String description) {
  log.debug "parse() >> zwave.parse($description)"
  def result = null
  if (description != "updated") {
    log.debug "Description is not updated"
    def cmd = zwave.parse(description)
    if (cmd) {
      result = zwaveEvent(cmd)
      log.debug "Parsed ${cmd} to ${result.inspect()}"
    } else {
      log.debug "Non-parsed event: ${description}"
    }
  }
  result
}

/**
  *
  *
  *  zwave event handlers
  *
  *
  **/
  
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
  log.debug "BasicReport"
  dimmerEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
  log.debug "BasicSet"
  dimmerEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd) {
  log.debug "SwitchMultilevelReport"
  dimmerEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelSet cmd) {
  log.debug "SwitchMultilevelSet"
  dimmerEvents(cmd)
}

private dimmerEvents(physicalgraph.zwave.Command cmd) {
  log.debug "dimmerEvents() $cmd"
  def value = (cmd.value ? "on" : "off")
  def result = [createEvent(name: "switch", value: value)]
  if (cmd.value && cmd.value <= 100) {
    result << createEvent(name: "level", value: cmd.value, unit: "%")
  }
  return result
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
  log.debug "ConfigurationReport $cmd"
  def name = ""
  def value = ""
  def reportValue = cmd.configurationValue[0]
  switch (cmd.parameterNumber) {
    case 3:
        name = "indicatorStatus"
        value = reportValue == 1 ? "when on" : reportValue == 2 ? "never" : "when off"
        break
    case 4:
        name = "invertSwitch"
        value = reportValue == 1 ? "true" : "false"
        break
    case 5:
        name = "ignoreStartLevel"
        value = reportValue == 1 ? "true" : "false"
        break
    case 7:
        name = "steps"
        value = reportValue
        break
    case 8:
        name = "delay"
        value = reportValue
        break
    case 9:
        name = "steps"
        value = reportValue
        break
    case 10:
        name = "delay"
        value = reportValue
        break
    case 11:
        name = "steps"
        value = reportValue
        break
    case 12:
        name = "delay"
        value = reportValue
        break
    default:
        break
  }
  createEvent([name: name, value: value])
}

def zwaveEvent(physicalgraph.zwave.commands.hailv1.Hail cmd) {
  log.debug "Hail"
  createEvent([name: "hail", value: "hail", descriptionText: "Switch button was pressed", displayed: false])
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
  log.debug "manufacturerId:   ${cmd.manufacturerId}"
  log.debug "manufacturerName: ${cmd.manufacturerName}"
  log.debug "productId:        ${cmd.productId}"
  log.debug "productTypeId:    ${cmd.productTypeId}"
  def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
  updateDataValue("MSR", msr)
  updateDataValue("manufacturer", cmd.manufacturerName)
  createEvent([descriptionText: "$device.displayName MSR: $msr", isStateChange: false])
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelStopLevelChange cmd) {
  log.debug "SwitchMultilevelStopLevelChange"
  [createEvent(name:"switch", value:"on"), response(zwave.switchMultilevelV3.switchMultilevelGet().format())]
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
  log.debug "Other z-wave command $cmd"
  // Handles all Z-Wave commands we aren't interested in
  [:]
}

/**
  *
  *
  *  Other functions
  *
  *
  **/
  
def on() {
  log.debug "on()"
  delayBetween([
      zwave.basicV1.basicSet(value: 0xFF).format(),
      zwave.switchMultilevelV3.switchMultilevelGet().format()
  ],5000)
}

def off() {
  log.debug "off()"
  delayBetween([
      zwave.basicV1.basicSet(value: 0x00).format(),
      zwave.switchMultilevelV3.switchMultilevelGet().format()
  ],5000)
}

def setLevel(value) {
  log.debug "setLevel >> value: $value"
  def valueaux = value as Integer
  def level = Math.max(Math.min(valueaux, 99), 0)
  setLevel(value, 0)
}

def setLevel(value, duration) {
  log.debug "setLevel >> value: $value, duration: $duration"
  def valueaux = value as Integer
  def level = Math.max(Math.min(valueaux, 99), 0)
  def dimmingDuration = duration < 128 ? duration : 128 + Math.round(duration / 60)
  def getStatusDelay = duration < 128 ? (duration*1000)+2000 : (Math.round(duration / 60)*60*1000)+2000
  delayBetween ([zwave.switchMultilevelV3.switchMultilevelSet(value: level, dimmingDuration: dimmingDuration).format(),
           zwave.switchMultilevelV3.switchMultilevelGet().format()], getStatusDelay)
}

def poll() {
  log.debug "poll()"
  zwave.switchMultilevelV3.switchMultilevelGet().format()
}

/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
  log.debug "ping()"
  refresh()
}

def refresh() {
  log.debug "refresh()"
  def commands = []
  commands << zwave.switchMultilevelV3.switchMultilevelGet().format()
  if (getDataValue("MSR") == null) {
    commands << zwave.manufacturerSpecificV2.manufacturerSpecificGet().format()
  }
  commands << new physicalgraph.device.HubAction(zwave.configurationV2.configurationGet(parameterNumber: 3).format())
  commands << new physicalgraph.device.HubAction(zwave.configurationV2.configurationGet(parameterNumber: 4).format())
  commands << new physicalgraph.device.HubAction(zwave.configurationV2.configurationGet(parameterNumber: 5).format())
  commands << new physicalgraph.device.HubAction(zwave.configurationV2.configurationGet(parameterNumber: 7).format())
  commands << new physicalgraph.device.HubAction(zwave.configurationV2.configurationGet(parameterNumber: 8).format())
  commands << new physicalgraph.device.HubAction(zwave.configurationV2.configurationGet(parameterNumber: 9).format())
  commands << new physicalgraph.device.HubAction(zwave.configurationV2.configurationGet(parameterNumber: 10).format())
  commands << new physicalgraph.device.HubAction(zwave.configurationV2.configurationGet(parameterNumber: 11).format())
  commands << new physicalgraph.device.HubAction(zwave.configurationV2.configurationGet(parameterNumber: 12).format())
  delayBetween(commands,100)
}

/**
  *
  *
  * Tile handlers
  *
  *
  **/

def invertSwitch(invert=true) {
  log.debug "invertSwitch() >> $invert"
  if (invert) {
    zwave.configurationV2.configurationSet(configurationValue: [1], parameterNumber: 4, size: 1).format()
  }
  else {
    zwave.configurationV2.configurationSet(configurationValue: [0], parameterNumber: 4, size: 1).format()
  }
}

void indicatorWhenOn() {
  log.debug "indicatorWhenOn()"
  sendEvent(name: "indicatorStatus", value: "when on", displayed: false)
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [1], parameterNumber: 3, size: 1).format()))
}

void indicatorWhenOff() {
  log.debug "indicatorWhenOff()"
  sendEvent(name: "indicatorStatus", value: "when off", displayed: false)
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [0], parameterNumber: 3, size: 1).format()))
}

void indicatorNever() {
  log.debug "indicatorNever()"
  sendEvent(name: "indicatorStatus", value: "never", displayed: false)
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [2], parameterNumber: 3, size: 1).format()))
}

def ignoreStartLevelOff() {
  log.debug "ignoreStartLevelOff()"
  sendEvent(name: "ignoreStartLevel", value: "off", displayed: false)
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [0], parameterNumber: 5, size: 1).format()))
}

def ignoreStartLevelOn() {
  log.debug "ignoreStartLevelOn()"
  sendEvent(name: "ignoreStartLevel", value: "on", displayed: false)
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [1], parameterNumber: 5, size: 1).format()))
}

def setSteps(step) {
  log.debug "setSteps() >> $step"
  sendEvent(name: "steps", value: "$step", displayed: false)
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [step], parameterNumber: 7, size: 1).format()))
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [step], parameterNumber: 9, size: 1).format()))
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [step], parameterNumber: 11, size: 1).format()))
}

def setManualDelay(delay) {
  log.debug "setDelay() >> $delay"
  sendEvent(name: "delay", value: "$delay", displayed: false)
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [delay], parameterNumber: 8, size: 1).format()))
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [delay], parameterNumber: 10, size: 1).format()))
  sendHubCommand(new physicalgraph.device.HubAction(zwave.configurationV2.configurationSet(configurationValue: [delay], parameterNumber: 12, size: 1).format()))
}
