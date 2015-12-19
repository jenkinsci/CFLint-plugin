package org.jenkinsci.plugins.cflint;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.kohsuke.stapler.DataBoundConstructor;

//import com.cflint.CFLint;

public class CFLintBuilder extends Builder {

    private final String folder;
    private final String cflintFolder;
    private final String cflintExcludesFile;
    private final String otherArgs;

    @DataBoundConstructor
    public CFLintBuilder(String folder,String cflintFolder,String cflintExcludesFile,String otherArgs) {
        this.folder = folder.trim();
        this.cflintFolder = cflintFolder.trim();
        this.cflintExcludesFile = cflintExcludesFile.trim();
        this.otherArgs = otherArgs.trim();
        }

    public String getFolder() {
    	return folder;
    }
    public String getCflintFolder() {
    	return cflintFolder;
    }
    public String getCflintExcludesFile() {
    	return cflintExcludesFile;
    }
    public String getOtherArgs() {
    	return otherArgs;
    }
    
    @Extension
    public static class Descriptor extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return FreeStyleProject.class.isAssignableFrom(jobType);
        }

        @Override
        public String getDisplayName() {
            return "execute CFLint analysis";
        }
    }
    
    @Override
    public hudson.model.Descriptor<Builder> getDescriptor() {
        return new Descriptor();
    }
    
    /**
     * Performs the build step.
     * 
     * @param build
     * @param launcher
     * @param listener
     * @return  whether the process succeeded.
     * @throws IOException
     * @throws InterruptedException
     * @see hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
        throws IOException, InterruptedException
    {	
    	StringBuilder sb = new StringBuilder();
    	sb.append(new File(cflintFolder).toString());
    	sb.append(File.separator);
    	sb.append("bin");
    	sb.append(File.separator);
    	sb.append("cflint");
    	if (!launcher.isUnix()){
    		sb.append(".bat");	
    	}
		sb.append(" -folder ");	
    	sb.append(new File(folder).toString());
    	sb.append(" -xml");
    	if (!cflintExcludesFile.equals("")){    	
    		sb.append(" -filterFile ");
        	sb.append(new File(cflintExcludesFile).toString());
    	}
    	sb.append(" ");
    	sb.append(otherArgs);
    	try{
    		PrintStream out = listener.getLogger();
    		launcher.launch(sb.toString(), build.getBuildVariables(), listener.getLogger(), build.getWorkspace());
	    	return true;
    	}catch (Throwable t){
    		t.printStackTrace(listener.error("Error in CFLint"));
    		return false;
    	}
    }
}