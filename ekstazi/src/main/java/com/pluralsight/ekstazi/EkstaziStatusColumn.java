package com.pluralsight.ekstazi;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Cause;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Column showing whether last build was Ekstazi enabled or not, using a badge
public class EkstaziStatusColumn extends ListViewColumn {
    private static final class BuildNodeColumnDescriptor extends ListViewColumnDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.EkstaziStatusColumn_DisplayName();
        }

        @Override
        public ListViewColumn newInstance(final StaplerRequest request, final JSONObject formData)
                throws FormException {
            return new EkstaziStatusColumn();
        }

        @Override
        public boolean shownByDefault() {
            return false;
        }
    }

    @Extension
    public static final Descriptor<ListViewColumn> DESCRIPTOR = new BuildNodeColumnDescriptor();

    @Override
    public Descriptor<ListViewColumn> getDescriptor() {
        return DESCRIPTOR;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, String> getLastBuildCauses(Job job) {
        Run r = job.getLastBuild();

        if (r == null)                          //There was no lastBuild, life's a clean slate!
            return null;

        FilePath ekstaziDir = new FilePath(new File(r.getArtifactsDir() + "/.ekstazi"));

        boolean ekstaziEnabledforLastBuild = false;
        Map<String, String> statusEntry = new HashMap<String, String>();

        try {
            if (ekstaziDir.exists()) {
                ekstaziEnabledforLastBuild = true;
            }

        } catch (IOException e) {
            //Swallow exception
        } catch (InterruptedException e) {
            //Swallow exception
        }

        statusEntry.put(new EkstaziBadgeAction(ekstaziEnabledforLastBuild, false).getIcon(),
                        new EkstaziBadgeAction(ekstaziEnabledforLastBuild, false).getTooltip());

        return statusEntry;
    }
}
