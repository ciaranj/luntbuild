package net.mccg.lunt;

public class LuntProjectFixture {
	public static LuntProject createBasicLuntProject() {
		return new LuntProject("luntbuild", "luntPassword", "http://luntserver", "fakeProject", "fakeSchedule", "artifact.jar");
	}
}
