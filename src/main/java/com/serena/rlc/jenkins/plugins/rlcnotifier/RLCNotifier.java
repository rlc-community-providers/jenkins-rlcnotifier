/* ===========================================================================
 *  Copyright (c) 2016 Serena Software. All rights reserved.
 *
 *  Use of the Sample Code provided by Serena is governed by the following
 *  terms and conditions. By using the Sample Code, you agree to be bound by
 *  the terms contained herein. If you do not agree to the terms herein, do
 *  not install, copy, or use the Sample Code.
 *
 *  1.  GRANT OF LICENSE.  Subject to the terms and conditions herein, you
 *  shall have the nonexclusive, nontransferable right to use the Sample Code
 *  for the sole purpose of developing applications for use solely with the
 *  Serena software product(s) that you have licensed separately from Serena.
 *  Such applications shall be for your internal use only.  You further agree
 *  that you will not: (a) sell, market, or distribute any copies of the
 *  Sample Code or any derivatives or components thereof; (b) use the Sample
 *  Code or any derivatives thereof for any commercial purpose; or (c) assign
 *  or transfer rights to the Sample Code or any derivatives thereof.
 *
 *  2.  DISCLAIMER OF WARRANTIES.  TO THE MAXIMUM EXTENT PERMITTED BY
 *  APPLICABLE LAW, SERENA PROVIDES THE SAMPLE CODE AS IS AND WITH ALL
 *  FAULTS, AND HEREBY DISCLAIMS ALL WARRANTIES AND CONDITIONS, EITHER
 *  EXPRESSED, IMPLIED OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY
 *  IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, OF FITNESS FOR A
 *  PARTICULAR PURPOSE, OF LACK OF VIRUSES, OF RESULTS, AND OF LACK OF
 *  NEGLIGENCE OR LACK OF WORKMANLIKE EFFORT, CONDITION OF TITLE, QUIET
 *  ENJOYMENT, OR NON-INFRINGEMENT.  THE ENTIRE RISK AS TO THE QUALITY OF
 *  OR ARISING OUT OF USE OR PERFORMANCE OF THE SAMPLE CODE, IF ANY,
 *  REMAINS WITH YOU.
 *
 *  3.  EXCLUSION OF DAMAGES.  TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE
 *  LAW, YOU AGREE THAT IN CONSIDERATION FOR RECEIVING THE SAMPLE CODE AT NO
 *  CHARGE TO YOU, SERENA SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER,
 *  INCLUDING BUT NOT LIMITED TO DIRECT, SPECIAL, INCIDENTAL, INDIRECT, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, DAMAGES FOR LOSS OF
 *  PROFITS OR CONFIDENTIAL OR OTHER INFORMATION, FOR BUSINESS INTERRUPTION,
 *  FOR PERSONAL INJURY, FOR LOSS OF PRIVACY, FOR NEGLIGENCE, AND FOR ANY
 *  OTHER LOSS WHATSOEVER) ARISING OUT OF OR IN ANY WAY RELATED TO THE USE
 *  OF OR INABILITY TO USE THE SAMPLE CODE, EVEN IN THE EVENT OF THE FAULT,
 *  TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY, OR BREACH OF CONTRACT,
 *  EVEN IF SERENA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.  THE
 *  FOREGOING LIMITATIONS, EXCLUSIONS AND DISCLAIMERS SHALL APPLY TO THE
 *  MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW.  NOTWITHSTANDING THE ABOVE,
 *  IN NO EVENT SHALL SERENA'S LIABILITY UNDER THIS AGREEMENT OR WITH RESPECT
 *  TO YOUR USE OF THE SAMPLE CODE AND DERIVATIVES THEREOF EXCEED US$10.00.
 *
 *  4.  INDEMNIFICATION. You hereby agree to defend, indemnify and hold
 *  harmless Serena from and against any and all liability, loss or claim
 *  arising from this agreement or from (i) your license of, use of or
 *  reliance upon the Sample Code or any related documentation or materials,
 *  or (ii) your development, use or reliance upon any application or
 *  derivative work created from the Sample Code.
 *
 *  5.  TERMINATION OF THE LICENSE.  This agreement and the underlying
 *  license granted hereby shall terminate if and when your license to the
 *  applicable Serena software product terminates or if you breach any terms
 *  and conditions of this agreement.
 *
 *  6.  CONFIDENTIALITY.  The Sample Code and all information relating to the
 *  Sample Code (collectively "Confidential Information") are the
 *  confidential information of Serena.  You agree to maintain the
 *  Confidential Information in strict confidence for Serena.  You agree not
 *  to disclose or duplicate, nor allow to be disclosed or duplicated, any
 *  Confidential Information, in whole or in part, except as permitted in
 *  this Agreement.  You shall take all reasonable steps necessary to ensure
 *  that the Confidential Information is not made available or disclosed by
 *  you or by your employees to any other person, firm, or corporation.  You
 *  agree that all authorized persons having access to the Confidential
 *  Information shall observe and perform under this nondisclosure covenant.
 *  You agree to immediately notify Serena of any unauthorized access to or
 *  possession of the Confidential Information.
 *
 *  7.  AFFILIATES.  Serena as used herein shall refer to Serena Software,
 *  Inc. and its affiliates.  An entity shall be considered to be an
 *  affiliate of Serena if it is an entity that controls, is controlled by,
 *  or is under common control with Serena.
 *
 *  8.  GENERAL.  Title and full ownership rights to the Sample Code,
 *  including any derivative works shall remain with Serena.  If a court of
 *  competent jurisdiction holds any provision of this agreement illegal or
 *  otherwise unenforceable, that provision shall be severed and the
 *  remainder of the agreement shall remain in full force and effect.
 * ===========================================================================
 */

package com.serena.rlc.jenkins.plugins.rlcnotifier;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> This class implements the RLC publisher process by using the {@link
 * com.urbancode.com.serena.rlc.jenkins.plugins.rlcnotifier.RLCSite}. </p>
 */
public class RLCNotifier extends Notifier {

    /**
     * Hold an instance of the Descriptor implementation for the RLC Publisher.
     */
    @Extension
    public static final RLCNotifierDescriptor DESCRIPTOR = new RLCNotifierDescriptor();

    private String siteName;
    private Boolean useAnotherUser;
    private String anotherUser;
    private String anotherPassword;
    private Boolean skip = false;
    private Boolean unstableAsSuccess = false;
    private String executionId;
    private Map<String, String> envMap = null;

    /**
     * Default constructor
     */
    public RLCNotifier(String siteName, Boolean useAnotherUser, String anotherUser, String anotherPassword,
                       Boolean skip, Boolean unstableAsSuccess, String executionId) {
        this.siteName = siteName;
        this.useAnotherUser = useAnotherUser;
        this.anotherUser = anotherUser;
        this.anotherPassword = anotherPassword;
        this.skip = skip;
        this.unstableAsSuccess = unstableAsSuccess;
        this.executionId = executionId;
    }

    public String getSiteName() {
        String name = siteName;
        if (name == null) {
            RLCSite[] sites = DESCRIPTOR.getSites();
            if (sites.length > 0) {
                name = sites[0].getProfileName();
            }
        }
        return name;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setUseAnotherUser(boolean useAnotherUser) {
        this.useAnotherUser = useAnotherUser;
    }

    public boolean isUseAnotherUser() {
        return Boolean.TRUE.equals(this.useAnotherUser);
    }

    public String getAnotherUser() {
        return this.anotherUser;
    }

    public void setAnotherUser(String anotherUser) {
        this.anotherUser = anotherUser;
    }

    public String getAnotherPassword() {
        return this.anotherPassword;
    }

    public void setAnotherPassword(String anotherPassword) {
        this.anotherPassword = anotherPassword;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isUnstableAsSuccess() {
        return Boolean.TRUE.equals(this.unstableAsSuccess);
    }

    public void setUnstableAsSuccess(boolean unstableAsSuccess) {
        this.unstableAsSuccess = unstableAsSuccess;
    }

    public boolean isSkip() {
        return Boolean.TRUE.equals(this.skip);
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    /**
     * This method returns the configured RLCSite object which match the siteName of the RLCPublisher
     * instance. (see Manage Hudson and System Configuration point RLC)
     *
     * @return the matching RLCSite or null
     */
    public RLCSite getSite() {
        RLCSite[] sites = DESCRIPTOR.getSites();
        if (siteName == null && sites.length > 0) {
            // default
            return sites[0];
        }
        for (RLCSite site : sites) {
            if (site.getDisplayName().equals(siteName)) {
                return site;
            }
        }
        return null;
    }


    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    /**
     * {@inheritDoc}
     *
     * @param build
     * @param launcher
     * @param listener
     * @return
     * @throws InterruptedException
     * @throws java.io.IOException  {@inheritDoc}
     * @see hudson.tasks.BuildStep#perform(hudson.model.Build, hudson.Launcher, hudson.model.BuildListener)
     */

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        RLCSite rlcSite = getSite();
        String resolvedAnotherUser = null;
        String resolvedAnotherPassword = null;

        RLCNotifierCallable task = null;

        if (isUseAnotherUser())
        {
            resolvedAnotherUser = resolveVariables(getAnotherUser());
            resolvedAnotherPassword = resolveVariables(getAnotherPassword());
            rlcSite = new RLCSite(rlcSite.getProfileName(), rlcSite.getUrl(), resolvedAnotherUser, resolvedAnotherPassword);
            listener.getLogger().println("[RLC] Using a different user to access Serena RLC: " + rlcSite.getUser());
        }
        if (isSkip()) {
            listener.getLogger().println("[RLC] Skipping notification to Serena RLC.");
        }
        else {
            String rlcStatus;
            if (build.getResult() == Result.SUCCESS) {
                rlcStatus = "COMPLETED";
            } else if (build.getResult() == Result.UNSTABLE) {
                if (isUnstableAsSuccess()) rlcStatus = "COMPLETED";
                else rlcStatus = "FAILED";
            } else {
                rlcStatus = "FAILED";
            }
            envMap = build.getEnvironment(listener);

            String resolvedExecutionId = resolveVariables(getExecutionId());
            listener.getLogger().println("[RLC] Notifying RLC task with id: " + resolvedExecutionId);


            try {
                task = new RLCNotifierCallable(rlcSite, resolvedExecutionId, rlcStatus, listener);
                try {
                    launcher.getChannel().call(task);
                } catch (InterruptedException e) {
                    throw new Exception("[RLC] Error calling RLCNotifierCallable!");
                }
            }
            catch (Throwable th) {
                th.printStackTrace(listener.error("[RLC] Failed to notify RLC of job status:\n" + th));
                build.setResult(Result.UNSTABLE);
            }
        }

        return true;
    }

    private String resolveVariables(String input) {
        String result = input;
        if (input != null && input.trim().length() > 0) {
            Pattern pattern = Pattern.compile("\\$\\{[^}]*}");
            Matcher matcher = pattern.matcher(result);
            while (matcher.find()) {
                String key = result.substring(matcher.start() + 2, matcher.end() - 1);
                if (envMap.containsKey(key)) {
                    result = matcher.replaceFirst(Matcher.quoteReplacement(envMap.get(key)));
                    matcher.reset(result);
                }
            }
        }

        return result;
    }
}


