/*
 * Copyright 2024 Alan Littleford
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import com.mentalresonance.dust.core.actors.Actor
import com.mentalresonance.dust.core.actors.ActorBehavior
import com.mentalresonance.dust.core.actors.ActorRef
import com.mentalresonance.dust.core.actors.ActorSystem
import com.mentalresonance.dust.core.actors.PoisonPill
import com.mentalresonance.dust.core.actors.Props
import com.mentalresonance.dust.core.actors.lib.PipelineActor
import com.mentalresonance.dust.html.msgs.HtmlDocumentMsg
import com.mentalresonance.dust.http.actors.HttpClientPipeActor
import com.mentalresonance.dust.http.service.HttpRequestResponseMsg
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * An (almost) trivial web pipeline in Dust. Its Actors
 * <ul>
 *     <li>Get a web page</li>
 *     <li>Examine the page and extract the core text</li>
 *     <li>Display the text</li>
 *     <li>Stop the pipe</li>
 * </ul>
 */
@CompileStatic
class WebClientPipelineTest extends Specification {

	public static boolean gotdata = false

	/*
	  A HttpClientPipeActor which gets a web page, wraps in an HttpRequestResponse message and
	  sends it up the pipe
	 */
	static class PageActor extends HttpClientPipeActor {

		static Props props() {
			Props.create(PageActor.class)
		}

		@Override
		void preStart() {
			request('https://www.computerworld.com/article/3535579/whats-behind-the-return-to-office-demands.html')
		}
	}

	@Slf4j
	static class FilterActor extends Actor {

		static Props props() {
			Props.create(FilterActor.class)
		}

		@Override
		ActorBehavior createBehavior() {
			(Serializable message) -> {
				switch(message) {
					/*
						Following the Actor philosophy we extract the html from the web page
						but then generate a new message to actually process it
					 */
					case HttpRequestResponseMsg:
						HtmlDocumentMsg msg = new HtmlDocumentMsg(self)
						msg.html = ((HttpRequestResponseMsg)message).response.body().string()
						tellSelf(msg)
						break

					case HtmlDocumentMsg:
						parent.tell(((HtmlDocumentMsg)message).extractContent(), self)
						break
				}
			}
		}
	}

	@Slf4j
	static class PipeLogActor extends Actor {

		static Props props() {
			Props.create(PipeLogActor.class)
		}

		@Override
		ActorBehavior createBehavior() {
			(Serializable message) -> {
				log.info message.toString()
				gotdata = true
				parent.tell(message, self)
			}
		}
	}

	@Slf4j
	static class PipeStopActor extends Actor {

		static Props props() {
			Props.create(PipeStopActor.class)
		}

		@Override
		ActorBehavior createBehavior() {
			(Serializable message) -> {
				log.info "Stopping pipe"
				parent.tell(new PoisonPill(), self)
			}
		}
	}

	def "WebPipe"() {
		when:
			ActorSystem system = new ActorSystem("Test")
			ActorRef pipe = system.context.actorOf(PipelineActor.props(
				// Create a pipeline which
				[
					PageActor.props(),     // Gets the html of a web page which passes to
					FilterActor.props(),   // Attempts to get core content of web page as plain text which passes to
					PipeLogActor.props(),  // A logger which writes out the text which passes the text on to
					PipeStopActor.props()  // An Actor which upon receipt of any message sends poison pil;l to pipe ...
				]
			))
			pipe.waitForDeath() // ... which dies
			system.stop()
		then:
			gotdata
	}
}