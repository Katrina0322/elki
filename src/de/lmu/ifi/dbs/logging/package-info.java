/**
<p>Logging facility for controlling logging behaviour of the complete framework.</p>
<h3>Logging</h3>
Associating a logger to a specific class, use something like the following code:
<br><code>
&#x002F;**<br>
<nbsp> * Holds the class specific debug status.<br>
<nbsp> *&#x002F;<br>
private static final boolean DEBUG = LoggingConfiguration.DEBUG;<br>
&#x002F;**<br>
<nbsp> * The logger of this class.<br>
<nbsp> *&#x002F;<br>
private Logger logger = Logger.getLogger(this.getClass().getName());<br>
</code>
<h4>Level specific logging</h4>
<p><ul>
    <li>Debugging: for debugging messages use levels below {@link java.util.logging.Level#INFO INFO},
        such as {@link java.util.logging.Level#FINE FINE},
        {@link java.util.logging.Level#FINER FINER},
        or {@link java.util.logging.Level#FINEST FINEST}.
    </li>
    <li>Verbose messages for regular user information: for &quot;verbose&quot; messages
        use the level {@link java.util.logging.Level#INFO INFO}.
    </li>
    <li>Warning messages for user information: For warning messages use the level {@link java.util.logging.Level#WARNING WARNING}.
    </li>
    <li>Exception messages: For Exception messages use the level {@link java.util.logging.Level#SEVERE SEVERE}.
        <ul>
            <li>Regular user information: Regularly,
            	the user should get information via the severe log message, that conveniently
            	could equal the message of the exception that caused logging the entry.
            </li>
            <li>Detailed information:
            	In debug mode, furthermore, the name of the exception
            	that caused logging the severe log-entry and the stacktrace should be
            	provided. Thus, logging a severe log-entry should include the exception.
            	Use e.g. something like the following code:
<pre>
catch(SomeException e)
{
	logger.log(Level.SEVERE, e.getMessage(), e);
}
</pre>
            </li>
        </ul>
    </li>
</ul></p>
<h3>Handling</h3>
<h4>Level specific handling</h4>
<p><ul>
    <li>Debugging: A handler responsible for debug messages should
    	process only LogRecords of level {@link java.util.logging.Level#FINE FINE},
    	{@link java.util.logging.Level#FINER FINER},
    	or {@link java.util.logging.Level#FINEST FINEST} (e.g. by using
    	{@link de.lmu.ifi.dbs.logging.DebugFilter DebugFilter}).
    	Additionally to setting the global debug mode to <code>false</code>,
    	the {@link de.lmu.ifi.dbs.logging.DebugFilter DebugFilter} can be configured to process messages
    	above a certain level, but below level {@link java.util.logging.Level#FINE FINE}. Thus,
    	the developer can make finegrained use of levels
    	{@link java.util.logging.Level#FINE FINE},
    	{@link java.util.logging.Level#FINER FINER},
		and {@link java.util.logging.Level#FINEST FINEST}.
    </li>
    <li>Verbose messages for regular user information:
    	A handler responsible for regular user information should process
    	only LogRecords of level {@link java.util.logging.Level#INFO INFO}. The user should
    	get provided with the pure message of the record.
    </li>
    <li>Warning messages for user information:
    	A handler responsible for occuring user warnings should process
    	only LogRecords of level {@link java.util.logging.Level#WARNING WARNING}. The user should
    	get provided with the pure message of the record.
    </li>
    <li>Exception messages
        <ul>
        	The handler responsible for exception messages should process
        	only LogRecords of level {@link java.util.logging.Level#SEVERE SEVERE}.
            <li>Regular user information:
            	If <code>DEBUG</code> is false, the handler responsible for exception
            	messages should provide the message of the LogEntry only.
            </li>
            <li>In debug mode, one should get provided with more detailed
	            information, as the name of the causing exception and the stacktrace.
            </li>
        </ul>
    </li>
</ul></p>
<h4>Handling for Command Line Interface</h4>
<p><ul>
    <li>Debugging:
    	Debug messages are printed immediately to <code>System.err</code>.
    </li>
    <li>Verbose messages for regular user information
    	are printed immediately to <code>System.out</code>.
    </li>
    <li>Warning messages for user information
    	 are printed immediately to <code>System.err</code>.
    </li>
    <li>Exception messages are printed immediately to <code>System.err</code>.
    </li>
</ul></p>
<h4>Handling for Graphical User Interface</h4>
TODO general comment for GUI logging facility
<p><ul>
    <li>Debugging - should use a log-file (in simple format?)</li>
    <li>Verbose messages for regular user information - should be printed on some progress textframe.
        Perhaps the logging facility can be enhanced to provide progressbars based on the logs?</li>
    <li>Warning messages for user information - should prompt a warning frame</li>
    <li>Exception messages - should prompt an error frame
        <ul>
            <li>Regular user information - should be displayed on the error frame</li>
            <li>Detailed information - could possibly be available by a request button
            	on the error frame.
            </li>
        </ul>
    </li>
</ul></p>
<h3>Efficiency considerations (for development)</h3>
<p>Coding the logging of debugging messages conditionally by a final boolean variable
    can result in a considerable speedup if the final boolean is false.
    This is the purpose of the recommendation given above,
    to specify a variable in each class
    that owns a logger:
<pre>
private static final boolean DEBUG = LoggingConfiguration.DEBUG;
</pre>
    A debugging message can then get coded as follows:
<pre>
if(DEBUG)
{
    logger.fine("message\n"); // or: finer, finest
}
</pre>
	Most compilers will remove the code in the <code>if</code>-clause during compiling
	if it can be taken for sure that the condition will always be <code>false</code>
	(e.g. if it is a <code>final boolean</code> set to <code>false</code>).
    For class specific debugging, the developer will code the attribute as
<pre>
private static final boolean DEBUG = true;
</pre>
    while for general debugging the developer could also set the central attribute
    {@link de.lmu.ifi.dbs.logging.LoggingConfiguration#DEBUG LoggingConfiguration.DEBUG} to <code>true</code>.
    </p>
    <p>For reasons similar to debugging, the verbosity of an algorithm is restricted by
        a boolean variable rather than by choosing the granularity of loggers. Any verbose message
        for information of users is of the level
        {@link java.util.logging.Level#INFO INFO}. One could switch off the handler
        for verbose messages, but the logging of those messages will still be time consuming.
        Thus, the developer should log verbose messages also conditionally, like:
<pre>
if(verbose)
{
    logger.info("message\n");
}
</pre>
        </p>
*/
package de.lmu.ifi.dbs.logging;