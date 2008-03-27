package net.mccg.lunt;

import org.apache.tools.ant.BuildException;

import junit.framework.TestCase;

public class LuntDeployTaskTest extends TestCase {

	private LuntDeployTask task;
	private boolean isDeployCalled;
	
	protected void setUp() throws Exception {
		isDeployCalled = false;
	}
	
	public void testUsesCorrectURLToDeployWhenExecuting(){
		final String expectedURL = "myurl";
		task = new LuntDeployTask(){
			void deploy(String filepath) {
				assertEquals(expectedURL, filepath);
				isDeployCalled = true;
			}
			LuntProject getLuntProject() {
				return LuntProjectFixture.createBasicLuntProject();
			}
			LuntServiceImpl getLuntService(LuntProject luntProject) throws Exception {
				return createMockLuntService(expectedURL);
			}
		};
		task.execute();
		assertTrue(isDeployCalled);
	}
	
	public void testReThrowsExceptionsAsABuildExceptionWhenExecutionFails(){
		final String expectedURL = "myurl";
		task = new LuntDeployTask(){
			void deploy(String filepath) {
				assertEquals(expectedURL, filepath);
				isDeployCalled = true;
			}
			LuntProject getLuntProject() {
				return LuntProjectFixture.createBasicLuntProject();
			}
			LuntServiceImpl getLuntService(LuntProject luntProject) throws Exception {
				throw new Exception("my error");
			}
		};
		try {
		task.execute();
		fail();
		}catch(BuildException expected){
			assertEquals("java.lang.Exception: my error", expected.getMessage());
		}
		assertFalse(isDeployCalled);
	}
	
	private LuntServiceImpl createMockLuntService(final String getArtifactURLValue) throws Exception{
		return new LuntServiceImpl(null){
			void init(LuntProject project) throws Exception {}
			public String getArtifactUrl() throws Exception {
				return getArtifactURLValue;
			}
		};
	}
}
