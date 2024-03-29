package org.apache.maven.wagon;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.PlexusTestCase;
import org.apache.maven.wagon.repository.Repository;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

/**
 * Base class for command executor tests.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 */
public abstract class CommandExecutorTestCase
    extends PlexusTestCase
{
    public void testErrorInCommandExecuted()
        throws Exception
    {
        CommandExecutor exec = (CommandExecutor) lookup( CommandExecutor.ROLE );

        Repository repository = getTestRepository();

        AuthenticationInfo authenticationInfo = new AuthenticationInfo();
        authenticationInfo.setUserName( System.getProperty( "user.name" ) );

        exec.connect( repository, authenticationInfo );

        try
        {
            exec.executeCommand( "fail" );
            fail( "Command should have failed" );
        }
        catch ( CommandExecutionException e )
        {
            assertTrue( e.getMessage().trim().endsWith( "fail: command not found" ) );
        }
        finally
        {
            exec.disconnect();
        }
    }

    public void testExecuteSuccessfulCommand()
        throws Exception
    {
        CommandExecutor exec = (CommandExecutor) lookup( CommandExecutor.ROLE );

        Repository repository = getTestRepository();

        AuthenticationInfo authenticationInfo = new AuthenticationInfo();
        authenticationInfo.setUserName( System.getProperty( "user.name" ) );

        exec.connect( repository, authenticationInfo );

        try
        {
            exec.executeCommand( "ls" );
        }
        finally
        {
            exec.disconnect();
        }
    }

    protected abstract Repository getTestRepository();
}
