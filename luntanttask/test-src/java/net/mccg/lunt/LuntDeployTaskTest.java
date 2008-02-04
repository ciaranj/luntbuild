package net.mccg.lunt;

import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class LuntDeployTaskTest extends TestCase {

	private LuntDeployTask task;
	private boolean isDeployCalled;
	
	@Before
	@Override
	protected void setUp() throws Exception {
		isDeployCalled = false;
	}
	
	@Test
	public void testUsesCorrectURLToDeployWhenExecuting(){
		final String expectedURL = "myurl";
		task = new LuntDeployTask(){
			@Override
			void deploy(String filepath) {
				assertEquals(expectedURL, filepath);
				isDeployCalled = true;
			}
			@Override
			LuntProject getLuntProject() {
				return LuntProjectFixture.createBasicLuntProject();
			}
			@Override
			LuntServiceImpl getLuntService(LuntProject luntProject) throws Exception {
				return createMockLuntService(expectedURL);
			}
		};
		task.execute();
		assertTrue(isDeployCalled);
	}
	
	@Test
	public void testReThrowsExceptionsAsABuildExceptionWhenExecutionFails(){
		final String expectedURL = "myurl";
		task = new LuntDeployTask(){
			@Override
			void deploy(String filepath) {
				assertEquals(expectedURL, filepath);
				isDeployCalled = true;
			}
			@Override
			LuntProject getLuntProject() {
				return LuntProjectFixture.createBasicLuntProject();
			}
			@Override
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
			@Override
			void init(LuntProject project) throws Exception {}
			@Override
			public String getArtifactUrl() throws Exception {
				return getArtifactURLValue;
			}
		};
	}
}
