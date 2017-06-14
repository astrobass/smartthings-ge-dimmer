SmartThings Device Handler for the GE Dimmer Switch
===
Added tiles to be able to control additional attributes of the GE Dimmer Switch

Ignore Start Level Tile (Parameter 5)
---
Every "Dim" command includes a start level embedded in it. Setting this parameter to a value of 0 will cause the switch to dim or brighten from the start level embedded in the command.

Dim Rate Step Adjustment Tile (Parameters 7, 9, 11)
---
The number of steps (or levels) that the dimmer will change. The step range is between 1 and 99. When the step is set to 1, the switch will take 99 steps. When the step is set to 99, it will be a single step. While the step adjustment can be set separately for the SmartThings app, manual control of the switch, and all-on or all-off commands, this device handler treats them the same.

Dim Rate Timing Adjustment Tile (Parameters 8, 10, 12)
---
The timing of the steps that the dimmer will change. The timing range is between 1 and 255. The timing of the steps can be adjusted in 10 millisecond intervals. While the timing adjustment can be set separately for the SmartThings app, manual control of the switch, and all-on or all-off commands, this device handler treats them the same.
