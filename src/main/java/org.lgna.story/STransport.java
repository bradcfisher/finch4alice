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

import org.finch4alice.*;

public class STransport
extends SJointedModel {
    private final TransportImp implementation;

    public STransport(TransportResource resource) {
        this.implementation = resource.createImplementation(this);
    }

    @Override
    TransportImp getImplementation() {
        return this.implementation;
    }

	/* ALL CHANGES BELOW HERE */

	private static Object finchLock = new Object();
    //private static Finch finch;
	private static FinchHTTP finch;

	private FinchHTTP getFinch() {
        synchronized (finchLock) {
            if (finch == null)
                finch = new FinchHTTP();
// TODO: Support specifying the server base URL...
            return finch;
        }
    }

	@MethodTemplate
    public void finchBuzz(int frequency, int duration)
    {
		synchronized (finchLock) {
			getFinch().buzz(frequency, duration);
		}
    }

	@MethodTemplate
	public int[] finchGetLightSensors() {
		synchronized (finchLock) {
			return getFinch().getLightSensors();
		}
	}

	@MethodTemplate
	public int finchGetLeftLightSensor() {
		synchronized (finchLock) {
			return getFinch().getLeftLightSensor();
		}
	}

	@MethodTemplate
	public int finchGetRightLightSensor() {
		synchronized (finchLock) {
			return getFinch().getRightLightSensor();
		}
	}

	@MethodTemplate
	public boolean finchIsLeftLightSensor(int limit) {
		synchronized (finchLock) {
			return getFinch().isLeftLightSensor(limit);
		}
	}

	@MethodTemplate
	public boolean finchIsRightLightSensor(int limit) {
		synchronized (finchLock) {
			return getFinch().isRightLightSensor(limit);
		}
	}

/*
TODO: While we can add this method returning a Double[] (or double[]), it doesn't work within Alice (unable to assign to a variable of type Double[])
	@MethodTemplate
	public Double[] finchGetAccelerations() {
		synchronized (finchLock) {
			double[] tmp = getFinch().getAccelerations();
			return new Double[] { tmp[0], tmp[1], tmp[2] };
		}
	}
*/

	@MethodTemplate
	public double finchGetXAcceleration() {
		synchronized (finchLock) {
			return getFinch().getXAcceleration();
		}
	}

	@MethodTemplate
	public double finchGetYAcceleration() {
		synchronized (finchLock) {
			return getFinch().getYAcceleration();
		}
	}

	@MethodTemplate
	public double finchGetZAcceleration() {
		synchronized (finchLock) {
			return getFinch().getZAcceleration();
		}
	}

	@MethodTemplate
	public boolean finchIsBeakDown() {
		synchronized (finchLock) {
			return getFinch().isBeakDown();
		}
	}

	@MethodTemplate
	public boolean finchIsBeakUp() {
		synchronized (finchLock) {
			return getFinch().isBeakUp();
		}
	}

	@MethodTemplate
	public boolean finchIsLevel() {
		synchronized (finchLock) {
			return getFinch().isFinchLevel();
		}
	}

	@MethodTemplate
	public boolean finchIsUpsideDown() {
		synchronized (finchLock) {
			return getFinch().isFinchUpsideDown();
		}
	}

	@MethodTemplate
	public boolean finchIsLeftWingDown() {
		synchronized (finchLock) {
			return getFinch().isLeftWingDown();
		}
	}

	@MethodTemplate
	public boolean finchIsRightWingDown() {
		synchronized (finchLock) {
			return getFinch().isRightWingDown();
		}
	}

	@MethodTemplate
	public boolean finchIsShaken() {
		synchronized (finchLock) {
			return getFinch().isShaken();
		}
	}

	@MethodTemplate
	public boolean finchIsTapped() {
		synchronized (finchLock) {
			return getFinch().isTapped();
		}
	}

	@MethodTemplate
	public boolean[] finchGetObstacleSensors() {
		synchronized (finchLock) {
			return getFinch().getObstacleSensors();
		}
	}

	@MethodTemplate
	public boolean finchIsObstacleLeftSide() {
		synchronized (finchLock) {
			return getFinch().isObstacleLeftSide();
		}
	}

	@MethodTemplate
	public boolean finchIsObstacleRightSide() {
		synchronized (finchLock) {
			return getFinch().isObstacleRightSide();
		}
	}

	@MethodTemplate
	public boolean finchIsObstacle() {
		synchronized (finchLock) {
			return getFinch().isObstacle();
		}
	}

	@MethodTemplate
	public double finchGetTemperature() {
		synchronized (finchLock) {
			return getFinch().getTemperature();
		}
	}

	@MethodTemplate
	public boolean finchIsTemperature(double limit) {
		synchronized (finchLock) {
			return getFinch().isTemperature(limit);
		}
	}

	@MethodTemplate
    public void finchSetWheelVelocities(int leftVelocity, int rightVelocity)
    {
		synchronized (finchLock) {
			getFinch().setWheelVelocities(leftVelocity, rightVelocity);
		}
    }

	@MethodTemplate
    public void finchStopWheels()
    {
		synchronized (finchLock) {
			getFinch().stopWheels();
		}
    }

	@MethodTemplate
    public void finchSetLED(int red, int green, int blue)
    {
		synchronized (finchLock) {
			getFinch().setLED(red, green, blue);
		}
    }

	@MethodTemplate
    public void finchPlaySong(int songNumber, double frequencyMultiplier, double durationMultiplier)
    {
		String[] songs = new String[] {
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

		finchPlayRTTTL(songs[songNumber], frequencyMultiplier, durationMultiplier, false);
	}
	
	@MethodTemplate
    public void finchPlayRTTTL(String rtttl, double frequencyMultiplier, double durationMultiplier, boolean useComputerSpeakers)
    {
		Song song;
		try {
			song = Song.parseRTTTL( rtttl );
		} catch (Throwable e) {
			return;
		}

		durationMultiplier = durationMultiplier * song.getMillisecondsPerWholeNote();

		for (Note n : song.getNotes()) {
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

/*
	@MethodTemplate
    public void finchQuit()
    {
		synchronized (finchLock) {
			getFinch().quit();
		}
    }
*/

}

