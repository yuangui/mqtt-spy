/***********************************************************************************
 * 
 * Copyright (c) 2015 Kamil Baczkowicz
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 *    
 * The Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 
 *    Kamil Baczkowicz - initial API and implementation and/or initial documentation
 *    
 */
package pl.baczkowicz.spy.daemon;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.spy.common.generated.RunningMode;
import pl.baczkowicz.spy.common.generated.ScriptDetails;
import pl.baczkowicz.spy.common.generated.TestCasesSettings;
import pl.baczkowicz.spy.exceptions.SpyException;
import pl.baczkowicz.spy.exceptions.XMLException;
import pl.baczkowicz.spy.scripts.BaseScriptManager;
import pl.baczkowicz.spy.scripts.Script;
import pl.baczkowicz.spy.testcases.TestCaseManager;
import pl.baczkowicz.spy.testcases.TestCaseResult;
import pl.baczkowicz.spy.utils.ThreadingUtils;

public abstract class BaseDaemon implements IDaemon
{
	/** Diagnostic logger. */
	private final static Logger logger = LoggerFactory.getLogger(BaseDaemon.class);
	
	protected BaseScriptManager scriptManager;
	
	protected TestCaseManager testCaseManager;	

	public TestCaseResult runTestCase(final String testCaseLocation)	
	{
		return runTestCase(testCaseLocation, null, TestCaseManager.DEFAULT_STEP_INTERVAL);
	}	
	
	public TestCaseResult runTestCase(final String testCaseLocation, final Map<String, Object> args)	
	{
		return runTestCase(testCaseLocation, args, TestCaseManager.DEFAULT_STEP_INTERVAL);
	}	
	
	public TestCaseResult runTestCase(final String testCaseLocation, final Map<String, Object> args, final long stepInterval)	
	{
		testCaseManager.setStepInterval(stepInterval);
		return testCaseManager.addAndRunTestCase(testCaseLocation, args);
	}	
	
	public Script runScript(final String scriptLocation)
	{
		return runScript(scriptLocation, false, null);
	}
	
	public Script runScript(final String scriptLocation, final boolean async, final Map<String, Object> args)
	{
		return scriptManager.addAndRunScript(scriptLocation, async, args);
	}	

	public void stopScript(final Script script)
	{
		scriptManager.stopScript(script);		
	}	

	protected void stopScripts()
	{
		scriptManager.stopScripts();		
	}	
	
	public boolean start(final String configurationFile)
	{
		try
		{		
			initialise();
									
			loadAndRun(configurationFile);
			
			return true;
		}
		catch (XMLException e)
		{
			logger.error("Cannot load the daemon's configuration", e);
		}
		catch (SpyException e)
		{
			logger.error("Error occurred while connecting to server", e);
		}
		
		return false;
	}
		
	/**
	 * Tries to stop all running threads and close the connection.
	 */
	public void stop()
	{
		stopScripts();
		waitAndStop();
	}
	
	protected void runScripts(final List<ScriptDetails> scriptSettings, final TestCasesSettings testCasesSettings, 
			final RunningMode runningMode) throws SpyException
	{
		// Run all configured scripts
		final List<Script> backgroundScripts = scriptManager.addScripts(scriptSettings);
		for (final Script script : backgroundScripts)
		{
			logger.info("About to start background script " + script.getName());
			scriptManager.runScript(script, true);
		}
		
		// Run all tests, one by one
		if (testCasesSettings != null)
		{
			testCaseManager.setAutoExport(testCasesSettings.isExportResults());
			testCaseManager.loadTestCases(testCasesSettings.getLocation());
			while (!canPublish())
			{
				logger.debug("Client not connected yet - can't start test cases... [waiting another 1000ms]");
				ThreadingUtils.sleep(1000);
			}
			testCaseManager.runAllTestCases();
		}
		
		// If in 'scripts only' mode, exit when all scripts finished
		if (RunningMode.SCRIPTS_ONLY.equals(runningMode))
		{
			waitAndStop();
		}
	}
	
	protected void waitForScripts()
	{
		ThreadingUtils.sleep(1000);
		
		// Wait until all scripts have completed or got frozen
		while (scriptManager.areScriptsRunning())
		{
			logger.debug("Scripts are still running... [waiting another 1000ms]");
			ThreadingUtils.sleep(1000);
		}
		
		// Wait until all test cases have completed or got frozen
		while (testCaseManager.areTestCasesStillRunning())
		{
			logger.debug("Test cases are still running... [waiting another 1000ms]");
			ThreadingUtils.sleep(1000);
		}
	}	
	
	protected void displayGoodbyeMessage()
	{
		ThreadingUtils.sleep(1000);
		for (final Thread thread : Thread.getAllStackTraces().keySet())
		{
			logger.trace("Thread {} is still running", thread.getName());
		}
		logger.info("All tasks completed - bye bye...");
	}
	
	// #######	

	protected abstract void waitAndStop();
	
	public abstract void initialise() throws XMLException;
	
	public abstract void loadAndRun(final String configurationFile) throws SpyException;
	
	protected abstract boolean canPublish();	
}
