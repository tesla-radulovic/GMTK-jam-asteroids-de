# GMTK-jam-asteroids-de
Submission for 2020 GMTK Game Jam

# [Game Page](https://visborne.itch.io/asteroids-definitive-edition)

# Description:

You are piloting a broken ship. You must escape the asteroid field ahead. You can pick up repairs. But you can only have two at a time.

WASD/Arrow Keys to move, at first only forward can be used to move (you need to repair the rest of your functionality, you don't have control of A/D by default). Also Arrow Keys and support for Dvorak (,AOE) and Serbian (ЊАСД)

A spin on the classic "space shooter" genre, where systems you normally have are broken and need to be repaired. "Power-ups" are replaced with "repairs" for features you should already have. However, you are limited to only two repairs at a time, forcing you to strategically repair your out-of-control ship.

Can you make it out of the asteroid field in time, before your life support runs out!?

Or never runs out (if you repair it).

Some notes about development:

The only libraries that this game uses is the Scala Standard Library and the Standard JavaScript HTML Canvas. I had wasted 4 hours trying to get PIXI.js to work with Scala.js but eventually had to scrap it and cut my losses. All of the physics and rotations were hand coded for the jam. There was even support for gravitational attraction among the asteroids in addition to the collision, however, they would all just clump up in the center of the screen which made the game impossible without the steering repair.

One of the scrapped ideas for a repair was an "escape pod" which would prevented ending the game upon loss of all health. Rather, you would just be teleported a thousand meters behind with your other repair. The idea was scrapped due to time constraints.

All of the repairs were meant to have equal strategic value, however, this is not the case with the retro-thrusters which seem to be kind of a dud. Originally, there was to be occasional walls that the player could hide behind to shield from asteroids/restrict movement and the retro-thrusters would greatly aid in navigating around the walls and taking cover in them.

Originally, you were meant to have three slots for repairs. However, play-testing showed that this would just lead to a Shields + Steering + Sensors combination, so the repairs were limited even further to promote more creative and situation based strategy. The time constraint was also made to be more difficult to encourage more creativity.

# Credits:

Font: https://www.fontspace.com/glasstown-nbp-font-f14742

Sounds: https://opengameart.org/content/nes-8-bit-sound-effects
