/*
 * Decompiled with CFR 0_102.
 */
package org.lgna.story;

import org.lgna.story.SJointedModel;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.implementation.TransportImp;
import org.lgna.story.resources.TransportResource;

import java.lang.Thread;
import org.lgna.project.annotations.MethodTemplate;

import com.finch4alice.*;

import edu.cmu.ri.createlab.terk.robot.finch.Finch;

/**
 * Override implementation of the org.lgna.story.STransport class which provides methods for
 * interacting with a Finch robot.
 */
public class STransport
	extends SJointedModel
{
    private final TransportImp implementation;

    public STransport(TransportResource resource) {
        this.implementation = resource.createImplementation(this);
    }

    @Override
    TransportImp getImplementation() {
        return this.implementation;
    }

	/* ALL CHANGES BELOW HERE */

	/**
	 * Object used for acquiring a thread synchronization lock on the underlying FinchHTTP object.
	 */
	private static Object finchLock = new Object();

	/**
	 * Finch object instance used for communicating with the attached Finch robot.
	 * Only one instance is maintained, regardless of how many STransport subclass instances are
	 * created.
	 */
	private static Finch finch;

	/**
	 * Retrieves the Finch instance maintained by this class.
	 * A new instance will be created if one has not yet been.
	 *
	 * @return	The Finch instance maintained by this class.
	 */
	private Finch getFinch() {
        synchronized (finchLock) {
            if (finch == null)
				finch = new Finch();

            return finch;
        }
    }

	/**
	 * Plays a tone at the specified frequency for the specified duration on the Finch's
	 * internal buzzer.
	 *
	 * <p>Middle C is about 262Hz. Visit <a href="http://www.phy.mtu.edu/~suits/notefreqs.html">http://www.phy.mtu.edu/~suits/notefreqs.html</a> for frequencies
	 * of musical notes.</p>
	 *
	 * <p>Note that buzz is non-blocking - so if you call two buzz methods in a row without an
	 * intervening sleep, you will only hear the second buzz (it will over-write the first buzz).</p>
	 *
	 * @param	frequency	Frequency in Hertz of the tone to be played
	 * @param	duration	Duration in milliseconds of the tone
	 */
	@MethodTemplate
    public void finchBuzz(int frequency, int duration) {
		synchronized (finchLock) {
			getFinch().buzz(frequency, duration);
		}
    }

	/**
	 * Returns a 2 integer array containing the current values of both light sensors.
	 * The left sensor is the 0th array element, and the right sensor is the 1st element.
	 *
	 * @return	A 2 int array containing both light sensor readings.
	 */
	@MethodTemplate
	public int[] finchGetLightSensors() {
		synchronized (finchLock) {
			return getFinch().getLightSensors();
		}
	}

	/**
	 * Returns the value of the left light sensor. Valid values range from 0 to 255, with higher
	 * values indicating more light is being detected by the sensor.
	 *
	 * @return	The current light level at the left light sensor
	 */
	@MethodTemplate
	public int finchGetLeftLightSensor() {
		synchronized (finchLock) {
			return getFinch().getLeftLightSensor();
		}
	}

	/**
	 * Returns the value of the right light sensor. Valid values range from 0 to 255, with higher
	 * values indicating more light is being detected by the sensor.
	 *
	 * @return	The current light level at the right light sensor
	 */
	@MethodTemplate
	public int finchGetRightLightSensor() {
		synchronized (finchLock) {
			return getFinch().getRightLightSensor();
		}
	}

	/**
	 * Returns {@code true} if the specified {@code limit} is less than the left light sensor value.
	 *
	 * @param	limit	The value the light sensor needs to exceed
	 * @return	whether the light sensor exceeds the value specified by {@code limit}
	 */
	@MethodTemplate
	public boolean finchIsLeftLightSensor(int limit) {
		synchronized (finchLock) {
			return getFinch().isLeftLightSensor(limit);
		}
	}

	/**
	 * Returns {@code true} if the specified {@code limit} is less than the right light sensor value.
	 *
	 * @param	limit	The value the light sensor needs to exceed
	 * @return	whether the light sensor exceeds the value specified by {@code limit}
	 */
	@MethodTemplate
	public boolean finchIsRightLightSensor(int limit) {
		synchronized (finchLock) {
			return getFinch().isRightLightSensor(limit);
		}
	}

/*
TODO: While we can add this method returning a Double[] (or double[]), it doesn't work within Alice (unable to assign to a variable of type Double[])

	/ **
	 * Use this method to simultaneously return the current X, Y, and Z accelerations experienced
	 * by the robot.
	 *
	 * <p>Values for acceleration can be in the range of -1.5g to +1.5g. When the robot is on a flat
	 * surface, X and Y should be close to 0g, and Z should be near +1.0g.</p>
	 *
	 * @return	an array of 3 doubles containing the X, Y, and Z acceleration values.  If unable to
	 *			connect, returns {@code null}.
	 * /
	@MethodTemplate
	public Double[] finchGetAccelerations() {
		synchronized (finchLock) {
			double[] tmp = getFinch().getAccelerations();
			if (tmp == null)
				return null;

			return new Double[] { tmp[0], tmp[1], tmp[2] };
		}
	}
*/

	/**
	 * Use this method to simultaneously return the current X, Y, and Z accelerations experienced
	 * by the robot.
	 *
	 * <p>Values for acceleration can be in the range of -1.5g to +1.5g. When the robot is on a flat
	 * surface, X and Y should be close to 0g, and Z should be near +1.0g.</p>
	 *
	 * @param	accelerations	an array of 3 doubles in which to store the X, Y, and Z acceleration
	 *							values.
	 *
	 * @return	{@code true} if successful, {@code false} if the array is not large enough or unable
	 *			to connect to the Finch.
	 */
	@MethodTemplate
	public boolean finchGetAccelerations(Double[] accelerations) {
		if (accelerations == null || accelerations.length < 3)
			return false;

		synchronized (finchLock) {
			double[] tmp = getFinch().getAccelerations();
			if (tmp == null)
				return false;

			accelerations[0] = tmp[0];
			accelerations[1] = tmp[1];
			accelerations[2] = tmp[2];

			return true;
		}
	}

	/**
	 * This method returns the current X-axis acceleration value experienced by the robot.
	 *
	 * <p>Values for acceleration range from -1.5 to +1.5g. The X-axis is the beak-tail axis, with
	 * positive values indicating acceleration in the direction of the beak.</p>
	 *
	 * @return	The X-axis acceleration value.  If unable to connect, returns {@code NaN}.
	 */
	@MethodTemplate
	public double finchGetXAcceleration() {
		synchronized (finchLock) {
			return getFinch().getXAcceleration();
		}
	}

	/**
	 * This method returns the current Y-axis acceleration value experienced by the robot.
	 *
	 * <p>Values for acceleration range from -1.5 to +1.5g. The Y-axis is the wheel-to-wheel axis,
	 * with positive values indicating acceleration in the direction of the right wheel.</p>
	 *
	 * @return	The Y-axis acceleration value.  If unable to connect, returns {@code NaN}.
	 */
	@MethodTemplate
	public double finchGetYAcceleration() {
		synchronized (finchLock) {
			return getFinch().getYAcceleration();
		}
	}

	/**
	 * This method returns the current Z-axis acceleration value experienced by the robot.
	 *
	 * <p>Values for acceleration range from -1.5 to +1.5g. The Z-axis runs perpendicular to the
	 * Finch's circuit board, with positive values indicating acceleration toward the bottom of the
	 * Finch.</p>
	 *
	 * @return	The Z-axis acceleration value.  If unable to connect, returns {@code NaN}.
	 */
	@MethodTemplate
	public double finchGetZAcceleration() {
		synchronized (finchLock) {
			return getFinch().getZAcceleration();
		}
	}

	/**
	 * This method returns true if the beak is pointed at the floor, false otherwise
	 * @return	true if beak is pointed at the floor
	 */
	@MethodTemplate
	public boolean finchIsBeakDown() {
		synchronized (finchLock) {
			return getFinch().isBeakDown();
		}
	}

	/**
	 * This method returns true if the beak is up (Finch sitting on its tail), false otherwise
	 * @return	true if beak is pointed at ceiling
	 */
	@MethodTemplate
	public boolean finchIsBeakUp() {
		synchronized (finchLock) {
			return getFinch().isBeakUp();
		}
	}

	/**
	 * This method returns true if the Finch is on a flat surface
	 * @return	true if the Finch is level
	 */
	@MethodTemplate
	public boolean finchIsLevel() {
		synchronized (finchLock) {
			return getFinch().isFinchLevel();
		}
	}

	/**
	 * This method returns true if the Finch is upside down, false otherwise
	 * @return	true if Finch is upside down
	 */
	@MethodTemplate
	public boolean finchIsUpsideDown() {
		synchronized (finchLock) {
			return getFinch().isFinchUpsideDown();
		}
	}

	/**
	 * This method returns true if the Finch's left wing is pointed at the ground
	 * @return	true if Finch's left wing is down
	 */
	@MethodTemplate
	public boolean finchIsLeftWingDown() {
		synchronized (finchLock) {
			return getFinch().isLeftWingDown();
		}
	}

	/**
	 * This method returns true if the Finch's right wing is pointed at the ground
	 * @return	true if Finch's right wing is down
	 */
	@MethodTemplate
	public boolean finchIsRightWingDown() {
		synchronized (finchLock) {
			return getFinch().isRightWingDown();
		}
	}

/*
	/ **
	 * Returns true if the Finch has been shaken since the last call to the method.
	 * @return	true if the Finch was recently shaken
	 * /
	@MethodTemplate
	public boolean finchIsShaken() {
		synchronized (finchLock) {
			return getFinch().isShaken();
		}
	}
*/

/*
	/ **
	 * Returns true if the Finch has been tapped since the last call to the method.
	 * @return	true if the Finch was recently tapped
	 * /
	@MethodTemplate
	public boolean finchIsTapped() {
		synchronized (finchLock) {
			return getFinch().isTapped();
		}
	}
*/

	/**
	 * Returns the current orientation of the Finch.
	 * @return	When connected to a Finch, returns one of: {@code "Level"}, {@code "Upside Down"},
	 *			{@code "Beak Up"}, {@code "Beak Down"}, {@code "Left Wing Down"},
	 *			{@code "Right Wing Down"}, {@code "In Between"}.  If unable to connect, returns
	 *			{@code null}.
	 */
	@MethodTemplate
	public String finchGetOrientation() {
		synchronized (finchLock) {
			return "Level"; //getFinch().getOrientation();
		}
	}

	/**
	 * This method returns the current X-axis angle of the robot relative to a plane parallel to
	 * the ground.
	 *
	 * <p>Values for the angle are in the range [-Pi, Pi].  The X-axis is the beak-tail axis.</p>
	 *
	 * @return	The X-axis angle value in radians, with 0 meaning the Finch is level on the
	 *			beak-tail axis, negative values meaning the beak is angled downward, and positive
	 *			values meaning the beak is angled up.  If unable to connect, returns {@code NaN}.
	 */
	@MethodTemplate
	public double finchGetXOrientationAngle() {
		synchronized (finchLock) {
			return 0.0; //getFinch().getXOrientationAngle();
		}
	}

	/**
	 * This method returns the current Y-axis angle of the robot relative to a plane parallel to
	 * the ground.
	 *
	 * <p>Values for the angle are in the range [-Pi, Pi].  The Y-axis is the wheel-to-wheel
	 * axis.</p>
	 *
	 * @return	The Y-axis angle value in radians, with 0 meaning the Finch is level on the
	 *			wheel-to-wheel axis, negative values meaning the left wheel is angled downward, and
	 *			positive values meaning the left wheel is angled up.  If unable to connect, returns
	 *			{@code NaN}.
	 */
	@MethodTemplate
	public double finchGetYOrientationAngle() {
		synchronized (finchLock) {
			return 0.0; //getFinch().getYOrientationAngle();
		}
	}

/*
TODO: While we can add this method returning a Double[] (or double[]), it doesn't work within Alice (unable to assign to a variable of type Double[])

	/ **
	 * Use this method to simultaneously return the current X and Y orientation angles experienced
	 * by the robot.
	 *
	 * <p>Values for the angles are in the range [-Pi, Pi].  When the robot is on a flat surface,
	 * the X and Y angles should both be close to 0.</p>
	 *
	 * @return	an array of 2 doubles containing the X and Y orientation angle values.  For the
	 *			X-axis, negative values mean the beak is angled downward, and positive values mean
	 *			the beak is angled up.  For the Y-axis, negative values mean the left wheel is
	 *			angled downward, and positive values mean the left wheel is angled up.  If unable to
	 *			connect, returns {@code null}.
	 * /
	@MethodTemplate
	public double[] finchGetOrientationAngles() {
		synchronized (finchLock) {
			return getFinch().finchGetOrientationAngles();
		}
	}
*/

	/**
	 * Returns the value of both obstacle sensors as 2 element boolean array. The left sensor is
	 * the 0th element, and the right sensor is the 1st element.
	 * @return	The values of left and right obstacle sensors in a 2 element array
	 */
	@MethodTemplate
	public boolean[] finchGetObstacleSensors() {
		synchronized (finchLock) {
			return getFinch().getObstacleSensors();
		}
	}

	/**
	 * Returns {@code true} if there is an obstruction in front of the left side of the robot.
	 * @return	Whether an obstacle exists in front of the left side of the robot.
	 */
	@MethodTemplate
	public boolean finchIsObstacleLeftSide() {
		synchronized (finchLock) {
			return getFinch().isObstacleLeftSide();
		}
	}

	/**
	 * Returns {@code true} if there is an obstruction in front of the right side of the robot.
	 * @return	Whether an obstacle exists in front of the right side of the robot.
	 */
	@MethodTemplate
	public boolean finchIsObstacleRightSide() {
		synchronized (finchLock) {
			return getFinch().isObstacleRightSide();
		}
	}

	/**
	 * Returns {@code true} if either left or right obstacle sensor detect an obstacle.
	 * @return	Whether either obstacle sensor sees an obstacle.
	 */
	@MethodTemplate
	public boolean finchIsObstacle() {
		synchronized (finchLock) {
			return getFinch().isObstacle();
		}
	}

	/**
	 * The current temperature reading at the temperature probe.
	 *
	 * <p>The value returned is in Celsius.  To get Fahrenheit from Celsius, multiply the number by
	 * 1.8 and then add 32.</p>
	 *
	 * @return	The current temperature in degrees Celsius
	 */
	@MethodTemplate
	public double finchGetTemperature() {
		synchronized (finchLock) {
			return getFinch().getTemperature();
		}
	}

	/**
	 * Returns {@code true} if the temperature is less than the value specified by limit,
	 * {@code false} otherwise.
	 *
	 * @param	limit	The value the temperature needs to exceed in Celcius
	 * @return	{@code true} if the temperature exceeds the value specified by {@code limit}
	 */
	@MethodTemplate
	public boolean finchIsTemperature(double limit) {
		synchronized (finchLock) {
			return getFinch().isTemperature(limit);
		}
	}

	/**
	 * The current temperature reading at the temperature probe, in Fahrenheit.
	 * @return	The current temperature in degrees Fahrenheit
	 * @see #finchGetTemperature()
	 */
	@MethodTemplate
	public double finchGetTemperatureFahrenheit() {
		synchronized (finchLock) {
			return 0.0; //getFinch().getTemperatureFahrenheit();
		}
	}

	/**
	 * This method simultaneously sets the velocities of both wheels.
	 *
	 * <p>Current valid values range from -255 to 255; negative values cause a wheel to move backwards.</p>
	 *
	 * @param	leftVelocity	The velocity at which to move the left wheel
	 * @param	rightVelocity	The velocity at which to move the right wheel
	 */
	@MethodTemplate
    public void finchSetWheelVelocities(int leftVelocity, int rightVelocity) {
		synchronized (finchLock) {
			getFinch().setWheelVelocities(leftVelocity, rightVelocity);
		}
    }

	/**
	 * Stops both wheels.
	 */
	@MethodTemplate
    public void finchStopWheels() {
		synchronized (finchLock) {
			getFinch().stopWheels();
		}
    }

	/**
	 * Sets the color of the LED in the Finch's beak.
	 *
	 * <p>The LED can be any color that can be created by mixing red, green, and blue; turning on
	 * all three colors in equal amounts results in white light.</p>
	 *
	 * <p>Valid ranges for the red, green, and blue elements are 0 to 255.</p>
	 *
	 * @param	red		sets the intensity of the red element of the LED
	 * @param	green	sets the intensity of the green element of the LED
	 * @param	blue	sets the intensity of the blue element of the LED
	 */
	@MethodTemplate
    public void finchSetLED(int red, int green, int blue) {
		synchronized (finchLock) {
			getFinch().setLED(red, green, blue);
		}
    }

	@MethodTemplate
    public void finchPlayMelody(int melodyNumber, double frequencyMultiplier, double durationMultiplier) {
		String[] melodies = new String[] {
						"JingleBell:d=8,o=5,b=112:32p,a,a,4a,a,a,4a,a,c6,f.,16g,2a,a#,a#,a#.,16a#,a#,a,a.,16a,a,g,g,a,4g,4c6",
						"StarSpangledBanner:d=4,o=5,b=100:8g.,16e,c,e,g,2c6,8e6.,16d6,c6,e,f#,2g,g,e6.,8d6,c6,2b,8a.,16b,c6,c6,g,e,c",
						"HauntHouse: d=4,o=5,b=108: 2a4, 2e, 2d#, 2b4, 2a4, 2c, 2d, 2a#4, 2e., e, 1f4, 1a4, 1d#, 2e., d, 2c., b4, 1a4, 1p, 2a4, 2e, 2d#, 2b4, 2a4, 2c, 2d, 2a#4, 2e., e, 1f4, 1a4, 1d#, 2e., d, 2c., b4, 1a4",
						"TakeOnMe:d=4,o=4,b=160:8f#5,8f#5,8f#5,8d5,8p,8b,8p,8e5,8p,8e5,8p,8e5,8g#5,8g#5,8a5,8b5,8a5,8a5,8a5,8e5,8p,8d5,8p,8f#5,8p,8f#5,8p,8f#5,8e5,8e5,8f#5,8e5,8f#5,8f#5,8f#5,8d5,8p,8b,8p,8e5,8p,8e5,8p,8e5,8g#5,8g#5,8a5,8b5,8a5,8a5,8a5,8e5,8p,8d5,8p,8f#5,8p,8f#5,8p,8f#5,8e5,8e5",
						"dualingbanjos:d=4,o=5,b=200:8c#,8d,e,c#,d,b4,c#,d#4,b4,p,16c#6,16p,16d6,16p,8e6,8p,8c#6,8p,8d6,8p,8b,8p,8c#6,8p,8a,8p,b,p,a4,a4,b4,c#,d#4,c#,b4,p,8a,8p,8a,8p,8b,8p,8c#6,8p,8a,8p,8c#6,8p,8b",
						"Greensleaves:d=4,o=5,b=140:g,2a#,c6,d.6,8d#6,d6,2c6,a,f.,8g,a,2a#,g,g.,8f,g,2a,f,2d,g,2a#,c6,d.6,8e6,d6,2c6,a,f.,8g,a,a#.,8a,g,f#.,8e,f#,2g",
						"Indiana:d=4,o=5,b=250:e,8p,8f,8g,8p,1c6,8p.,d,8p,8e,1f,p.,g,8p,8a,8b,8p,1f6,p,a,8p,8b,2c6,2d6,2e6,e,8p,8f,8g,8p,1c6,p,d6,8p,8e6,1f.6,g,8p,8g,e.6,8p,d6,8p,8g,e.6,8p,d6,8p,8g,f.6,8p,e6,8p,8d6,2c6",
						"KnightRider:d=4,o=5,b=125:16e,16p,16f,16e,16e,16p,16e,16e,16f,16e,16e,16e,16d#,16e,16e,16e,16e,16p,16f,16e,16e,16p,16f,16e,16f,16e,16e,16e,16d#,16e,16e,16e,16d,16p,16e,16d,16d,16p,16e,16d,16e,16d,16d,16d,16c,16d,16d,16d,16d,16p,16e,16d,16d,16p,16e,16d,16e,16d,16d,16d,16c,16d,16d,16d",
						"munsters:d=4,o=5,b=160:d,8f,8d,8g#,8a,d6,8a#,8a,2g,8f,8g,a,8a4,8d#4,8a4,8b4,c#,8d,p,c,c6,c6,2c6,8a#,8a,8a#,8g,8a,f,p,g,g,2g,8f,8e,8f,8d,8e,2c#,p,d,8f,8d,8g#,8a,d6,8a#,8a,2g,8f,8g,a,8d#4,8a4,8d#4,8b4,c#,2d",
						"aadams:d=4,o=5,b=160:8c,f,8a,f,8c,b4,2g,8f,e,8g,e,8e4,a4,2f,8c,f,8a,f,8c,b4,2g,8f,e,8c,d,8e,1f,8c,8d,8e,8f,1p,8d,8e,8f#,8g,1p,8d,8e,8f#,8g,p,8d,8e,8f#,8g,p,8c,8d,8e,8f",
						"PinkPanther:d=4,o=5,b=160:8d#,8e,2p,8f#,8g,2p,8d#,8e,16p,8f#,8g,16p,8c6,8b,16p,8d#,8e,16p,8b,2a#,2p,16a,16g,16e,16d,2e",
						"ScoobyDoo:d=4,o=5,b=160:8e6,8e6,8d6,8d6,2c6,8d6,e6,2a,8a,b,g,e6,8d6,c6,8d6,2e6,p,8e6,8e6,8d6,8d6,2c6,8d6,f6,2a,8a,b,g,e6,8d6,2c6"
					};

		finchPlayRTTTL(melodies[melodyNumber], frequencyMultiplier, durationMultiplier, false);
	}

	/**
	 * Plays an RTTTL (Ring Tone Text Transfer Language) string on the Finch or the Computer's speaker.
	 *
	 * @param	rtttl				The RTTTL-encoded string 
	 * @param	frequencyMultiplier	Multiplier to apply to the note frequencies.  Use 1.0 to hear
	 *								the notes as intended, smaller values to play at a lower
	 *								frequency (for example 0.5 to lower by an octave), and larger
	 *								values to play at a higher frequency (like 2.0 to raise an
	 *								octave).
	 * @param	durationMultiplier	Multiplier to apply to the note durations.  A value of 1.0 will
	 *								play the notes with their typical duration, smaller values will
	 *								reduce the duration of each note, and higher values will
	 *								increase the duration.
	 * @param	useComputerSpeakers	Whether to play the notes through the computer's speakers (true)
	 *								or the Finch buzzer (false).
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Ring_Tone_Transfer_Language">Wikipedia entry on RTTTL</a>
	 * @see <a href="http://www.mobilefish.com/tutorials/rtttl/rtttl_quickguide_specification.html">Mobilefish RTTTL Tutorial</a>
	 */
	@MethodTemplate
    public void finchPlayRTTTL(
					String rtttl, double frequencyMultiplier,
					double durationMultiplier, boolean useComputerSpeakers
				)
	{
		Melody melody;
		try {
			melody = Melody.parseRTTTL( rtttl );
		} catch (Throwable e) {
			return;
		}

		durationMultiplier = durationMultiplier * melody.getMillisecondsPerWholeNote();

		for (Note n : melody.getNotes()) {
			int frequency = (int) (n.getFrequency() * frequencyMultiplier);
			int duration = (int) (n.getDurationFraction() * durationMultiplier);

			if (frequency != 0) {
				if (useComputerSpeakers)
					synchronized (finchLock) {
						getFinch().playTone(frequency, duration);
					}
				else {
					// It seems the Finch will ignore a new tone+duration if it is requested
					// while a tone is already playing.  However, if a duration of 0 is provided,
					// it will stop any current tone and allow a new tone to be set immediately.
					finchBuzz(0, 0);

					finchBuzz(frequency, duration);
				}
			}

			if (!useComputerSpeakers) {
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					// Interrupt thread again to ensure any following sleep is interrupted (eg. signal the thread to shutdown gracefully)
					Thread.currentThread().interrupt();
					break; // Exit the current loop, as the thread should terminate
				}
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// Interrupt thread again to ensure any following sleep is interrupted (eg. signal the thread to shutdown gracefully)
			Thread.currentThread().interrupt();
		}
    }

	/**
	 * Tests whether the Bird Brain Robot Server is running and a Finch is currently connected.
	 * @return	Returns {@code true} if the Bird Brain Robot Server can be contacted and responds with
	 *			status data for an attached Finch, otherwise returns {@code false}.
	 */
	@MethodTemplate
	public boolean finchIsConnected() {
		synchronized (finchLock) {
			//return getFinch().isConnected();
// TODO: make this work as expected, if possible
			return true;
		}
	}

/*
	@MethodTemplate
    public void finchQuit() {
		synchronized (finchLock) {
			getFinch().quit();
		}
    }
*/

}

