/**
 * 
 */
package io.jenkins.x.eclipse.plugin;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.jenkins.x.client.PipelineClient;
import io.jenkins.x.client.kube.PipelineActivity;

/**
 * @author suren
 *
 */
public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		PipelineClient client = PipelineClient.newInstance();
		client.start();
		
		final MessageConsole console = JenkinsConsole.getConsole();
		MessageConsoleStream msg = console.newMessageStream();
		
		client.addListener(new Watcher<PipelineActivity>() {

			@Override
			public void eventReceived(Action action, PipelineActivity activity) {
				ObjectMeta metadata = activity.getMetadata();
				
				String clusterName = metadata.getClusterName();
				String namespace = metadata.getNamespace();
				String name = metadata.getName();
				
				msg.println("Cluster: " + clusterName + " Namespace:" + namespace + " " + name + " action: " + action);
			}

			@Override
			public void onClose(KubernetesClientException e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public static void main(String[] args) throws InterruptedException {
		PipelineClient client = PipelineClient.newInstance();
		client.start();
		
		client.addListener(new Watcher<PipelineActivity>() {

			@Override
			public void eventReceived(Action action, PipelineActivity activity) {
//				JenkinsConsole.getConsole().newMessageStream().println(action + "===" + activity);
				System.out.println(action + "===" + activity);
			}

			@Override
			public void onClose(KubernetesClientException e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		while(true);
	}
}
