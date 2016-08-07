package com.finch4alice;

public interface FinchSensorState {

	/**
	 * Returns the value of the left light sensor. Valid values range from 0 to 255, with higher
	 * values indicating more light is being detected by the sensor.
	 *
	 * @return	The current light level at the left light sensor
	 */
	default public int getLeftLightSensor() {
		int[] lights = getLightSensors();
		return ((lights != null) ? lights[0] : 0);
	} // getLeftLightSensor

	/**
	 * Returns the value of the right light sensor. Valid values range from 0 to 255, with higher
	 * values indicating more light is being detected by the sensor.
	 *
	 * @return	The current light level at the right light sensor
	 */
	default public int getRightLightSensor() {
		int[] lights = getLightSensors();
		return ((lights != null) ? lights[1] : 0);
	} // getRightLightSensor

	/**
	 * Returns {@code true} if the specified {@code limit} is less than the left light sensor value.
	 *
	 * @param	limit	The value the light sensor needs to exceed
	 * @return	whether the light sensor exceeds the value specified by {@code limit}
	 */
	default public boolean isLeftLightSensor(double limit) {
		return (getLeftLightSensor() > limit);
	} // isLeftLightSensor

	/**
	 * Returns {@code true} if the specified {@code limit} is less than the right light sensor value.
	 *
	 * @param	limit	The value the light sensor needs to exceed
	 * @return	whether the light sensor exceeds the value specified by {@code limit}
	 */
	default public boolean isRightLightSensor(double limit) {
		return (getRightLightSensor() > limit);
	} // isRightLightSensor

	/**
	 * Returns a 2 integer array containing the current values of both light sensors.
	 * The left sensor is the 0th array element, and the right sensor is the 1st element.
	 *
	 * @return	A 2 int array containing both light sensor readings.
	 */
	public int[] getLightSensors();

	/**
	 * Returns {@code true} if there is an obstruction in front of the left side of the robot.
	 * @return	Whether an obstacle exists in front of the left side of the robot.
	 */
	default public boolean isObstacleLeftSide() {
		boolean[] obstacles = getObstacleSensors();
		return (obstacles != null) && obstacles[0];
	} // isObstacleLeftSide

	/**
	 * Returns {@code true} if there is an obstruction in front of the right side of the robot.
	 * @return	Whether an obstacle exists in front of the right side of the robot.
	 */
	default public boolean isObstacleRightSide() {
		boolean[] obstacles = getObstacleSensors();
		return (obstacles != null) && obstacles[1];
	} // isObstacleRightSide

	/**
	 * Returns {@code true} if either left or right obstacle sensor detect an obstacle.
	 * @return	Whether either obstacle sensor sees an obstacle.
	 */
	default public boolean isObstacle() {
		boolean[] obstacles = getObstacleSensors();
		return (obstacles != null) && (obstacles[0] || obstacles[1]);
	} // isObstacle

	/**
	 * Returns the value of both obstacle sensors as 2 element boolean array. The left sensor is
	 * the 0th element, and the right sensor is the 1st element.
	 * @return	The values of left and right obstacle sensors in a 2 element array
	 */
	public boolean[] getObstacleSensors();

	/**
	 * This method returns the current X-axis acceleration value experienced by the robot.
	 *
	 * <p>Values for acceleration range from -1.5 to +1.5g. The X-axis is the beak-tail axis, with
	 * positive values indicating acceleration in the direction of the beak.</p>
	 *
	 * @return	The X-axis acceleration value.  If unable to connect, returns {@code NaN}.
	 */
	default public double getXAcceleration() {
		double[] accelerations = getAccelerations();
		return ((accelerations != null) ? accelerations[0] : Double.NaN);
	} // getXAcceleration

	/**
	 * This method returns the current Y-axis acceleration value experienced by the robot.
	 *
	 * <p>Values for acceleration range from -1.5 to +1.5g. The Y-axis is the wheel-to-wheel axis,
	 * with positive values indicating acceleration in the direction of the right wheel.</p>
	 *
	 * @return	The Y-axis acceleration value.  If unable to connect, returns {@code NaN}.
	 */
	default public double getYAcceleration() {
		double[] accelerations = getAccelerations();
		return ((accelerations != null) ? accelerations[1] : Double.NaN);
	} // getYAcceleration

	/**
	 * This method returns the current Z-axis acceleration value experienced by the robot.
	 *
	 * <p>Values for acceleration range from -1.5 to +1.5g. The Z-axis runs perpendicular to the
	 * Finch's circuit board, with positive values indicating acceleration toward the bottom of the
	 * Finch.</p>
	 *
	 * @return	The Z-axis acceleration value.  If unable to connect, returns {@code NaN}.
	 */
	default public double getZAcceleration() {
		double[] accelerations = getAccelerations();
		return ((accelerations != null) ? accelerations[2] : Double.NaN);
	} // getZAcceleration

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
	default public double getXOrientationAngle() {
		double[] angles = getOrientationAngles();
		return ((angles != null) ? angles[0] : Double.NaN);
	} // getXOrientationAngle

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
	default public double getYOrientationAngle() {
		double[] angles = getOrientationAngles();
		return ((angles != null) ? angles[1] : Double.NaN);
	} // getYOrientationAngle

	/**
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
	 */
	default public double[] getOrientationAngles() {
		double[] accelerations = getAccelerations();
		if (accelerations == null)
			return null;

		double xAccel = accelerations[0];
		double yAccel = accelerations[1];
		double zAccel = accelerations[2];

		double xAngle = Math.atan2(-xAccel, zAccel);
		double yAngle = Math.atan2(-yAccel, zAccel);

		return new double[] { xAngle, yAngle };
	} // getOrientationAngles

	/**
	 * Use this method to simultaneously return the current X, Y, and Z accelerations experienced
	 * by the robot.
	 *
	 * <p>Values for acceleration can be in the range of -1.5g to +1.5g. When the robot is on a flat
	 * surface, X and Y should be close to 0g, and Z should be near +1.0g.</p>
	 *
	 * @return	an array of 3 doubles containing the X, Y, and Z acceleration values.  If unable to
	 *			connect, returns {@code null}.
	 */
	public double[] getAccelerations();

	/**
	 * Returns the current orientation of the Finch.
	 * @return	When connected to a Finch, returns one of: {@code "Level"}, {@code "Upside Down"},
	 *			{@code "Beak Up"}, {@code "Beak Down"}, {@code "Left Wing Down"},
	 *			{@code "Right Wing Down"}, {@code "In Between"}.  If unable to connect, returns
	 *			{@code null}.
	 */
	public String getOrientation();

	/**
	 * This method returns {@code true} if the beak is up (Finch sitting on its tail), {@code false} otherwise
	 * @return	{@code true} if beak is pointed at ceiling
	 */
	default public boolean isBeakUp() {
		return "Beak Up".equals(getOrientation());
	} // isBeakUp

	/**
	 * This method returns {@code true} if the beak is pointed at the floor, {@code false} otherwise
	 * @return	{@code true} if beak is pointed at the floor
	 */
	default public boolean isBeakDown() {
		return "Beak Down".equals(getOrientation());
	} // isBeakDown

	/**
	 * This method returns {@code true} if the Finch is on a flat surface
	 * @return	{@code true} if the Finch is level
	 */
	default public boolean isFinchLevel() {
		return "Level".equals(getOrientation());
	} // isFinchLevel

	/**
	 * This method returns {@code true} if the Finch is upside down, {@code false} otherwise
	 * @return	{@code true} if Finch is upside down
	 */
	default public boolean isFinchUpsideDown() {
		return "Upside Down".equals(getOrientation());
	} // isFinchUpsideDown

	/**
	 * This method returns {@code true} if the Finch's left wing is pointed at the ground
	 * @return	{@code true} if Finch's left wing is down
	 */
	default public boolean isLeftWingDown() {
		return "Left Wing Down".equals(getOrientation());
	} // isLeftWingDown

	/**
	 * This method returns {@code true} if the Finch's right wing is pointed at the ground
	 * @return	{@code true} if Finch's right wing is down
	 */
	default public boolean isRightWingDown() {
		return "Right Wing Down".equals(getOrientation());
	} // isRightWingDown

	/**
	 * The current temperature reading at the temperature probe.
	 *
	 * <p>The value returned is in Celsius.  To get Fahrenheit from Celsius, multiply the number by
	 * 1.8 and then add 32.</p>
	 *
	 * @return	The current temperature in degrees Celsius
	 * @see #getTemperatureFahrenheit()
	 */
	public double getTemperature();

	/**
	 * Returns {@code true} if the temperature is less than the value specified by limit,
	 * {@code false} otherwise.
	 *
	 * @param	limit	The value the temperature needs to exceed in Celcius
	 * @return	{@code true} if the temperature exceeds the value specified by {@code limit}
	 */
	default public boolean isTemperature(double limit) {
		// TODO: The javadoc on finchrobot.com is contradictory about what this function returns.
		//		Need to review source to determine actual behavior (eg. return true if > limit or
		//		if < limit?)
		return (getTemperature() > limit);
	} // isTemperature

	/**
	 * The current temperature reading at the temperature probe, in Fahrenheit.
	 * @return	The current temperature in degrees Fahrenheit
	 * @see #getTemperature()
	 */
	default public double getTemperatureFahrenheit() {
		return getTemperature() * 1.8 + 32;
	} // getTemperatureFahrenheit

	/**
	 * Retrieves a brief status report string containing the current status of the Finch.
	 * @return	A brief status report string containing the current status of the Finch.
	 */
	default public String getStatusReport() {
		int[] lights = getLightSensors();
		boolean[] obstacles = getObstacleSensors();
		double[] accelerations = getAccelerations();
		double temperature = getTemperature();
		double[] angles = getOrientationAngles();

		return "Sensors:\n"+
				"  Light:        left="+ lights[0] +" right="+ lights[1] +"\n"+
				"  Obstacle:     left="+ obstacles[0] +" right="+ obstacles[1] +"\n"+
				"  Acceleration: X="+ accelerations[0] +" Y="+ accelerations[1] +" Z="+ accelerations[2] +"\n"+
				"  Orientation:  "+ getOrientation() + "\n" +
				"      Angle X:  "+ angles[0] +" radians ("+ (angles[0] / Math.PI * 180) +" degrees)\n" +
				"      Angle Y:  "+ angles[1] +" radians ("+ (angles[1] / Math.PI * 180) +" degrees)\n" +
				"  Temperature:  "+ temperature +" C ("+ (temperature * 1.8 + 32) +" F)";
	} // getStatusReport

} // interface FinchSensorState
