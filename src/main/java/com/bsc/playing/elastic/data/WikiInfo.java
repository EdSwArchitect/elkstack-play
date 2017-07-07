package com.bsc.playing.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by EdwinBrown on 7/4/2017.
 */
public class WikiInfo {
    //{"action":"edit","changeSize":5,"flags":null,
    // "geo_ip":{"city":"Summerville","country_name":"United States","latitude":32.9647,"longitude":-80.2015,"region_name":"South Carolina"},
    // "hashtags":[],"is_anon":true,"is_bot":false,"is_minor":false,"is_new":false,"is_unpatrolled":false,"mentions":[],
    // "ns":"Main","page_title":"List of American film actresses",
    // "parent_rev_id":"788982571","rev_id":"788982490","summary":"/* A */",
    // "url":"https://en.wikipedia.org/w/index.php?diff=788982571&oldid=788982490",
    // "user":"2606:A000:EF0A:4200:91E2:371B:DE18:7602"}

    private String action;
    private int changeSize;
    private String flags;
    private GeoIp geoIp;
    private String[] hashtags;
    private boolean anon;
    private boolean bot;
    private boolean minor;
    private boolean isNew;
    private boolean unparolled;
    private String[] mentions;
    private String ns;
    private String pageTitle;
    private String parentRevId;
    private String revId;
    private String summary;
    private String url;
    private String user;

    public WikiInfo() {

    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @JsonProperty("change_size")
    public int getChangeSize() {
        return changeSize;
    }

    @JsonProperty("change_size")
    public void setChangeSize(int changeSize) {
        this.changeSize = changeSize;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    @JsonProperty("geo_ip")
    public GeoIp getGeoIp() {
        return geoIp;
    }

    @JsonProperty("geo_ip")
    public void setGeoIp(GeoIp geoIp) {
        this.geoIp = geoIp;
    }

    public String[] getHashtags() {
        return hashtags;
    }

    public void setHashtags(String[] hashtags) {
        this.hashtags = hashtags;
    }

    @JsonProperty("is_anon")
    public boolean isAnon() {
        return anon;
    }

    @JsonProperty("is_anon")
    public void setAnon(boolean anon) {
        this.anon = anon;
    }

    @JsonProperty("is_bot")
    public boolean isBot() {
        return bot;
    }

    @JsonProperty("is_bot")
    public void setBot(boolean bot) {
        this.bot = bot;
    }

    @JsonProperty("is_minor")
    public boolean isMinor() {
        return minor;
    }

    @JsonProperty("is_minor")
    public void setMinor(boolean minor) {
        this.minor = minor;
    }

    @JsonProperty("is_new")
    public boolean isNew() {
        return isNew;
    }

    @JsonProperty("is_new")
    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @JsonProperty("is_unpatrolled")
    public boolean isUnparolled() {
        return unparolled;
    }

    @JsonProperty("is_unpatrolled")
    public void setUnparolled(boolean unparolled) {
        this.unparolled = unparolled;
    }

    public String[] getMentions() {
        return mentions;
    }

    public void setMentions(String[] mentions) {
        this.mentions = mentions;
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    @JsonProperty("page_title")
    public String getPageTitle() {
        return pageTitle;
    }

    @JsonProperty("page_title")
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    @JsonProperty("parent_rev_id")
    public String getParentRevId() {
        return parentRevId;
    }

    @JsonProperty("parent_rev_id")
    public void setParentRevId(String parentRevId) {
        this.parentRevId = parentRevId;
    }

    @JsonProperty("rev_id")
    public String getRevId() {
        return revId;
    }

    @JsonProperty("rev_id")
    public void setRevId(String revId) {
        this.revId = revId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WikiInfo wikiInfo = (WikiInfo) o;

        if (changeSize != wikiInfo.changeSize) return false;
        if (anon != wikiInfo.anon) return false;
        if (bot != wikiInfo.bot) return false;
        if (minor != wikiInfo.minor) return false;
        if (isNew != wikiInfo.isNew) return false;
        if (unparolled != wikiInfo.unparolled) return false;
        if (action != null ? !action.equals(wikiInfo.action) : wikiInfo.action != null) return false;
        if (flags != null ? !flags.equals(wikiInfo.flags) : wikiInfo.flags != null) return false;
        if (geoIp != null ? !geoIp.equals(wikiInfo.geoIp) : wikiInfo.geoIp != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(hashtags, wikiInfo.hashtags)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(mentions, wikiInfo.mentions)) return false;
        if (ns != null ? !ns.equals(wikiInfo.ns) : wikiInfo.ns != null) return false;
        if (pageTitle != null ? !pageTitle.equals(wikiInfo.pageTitle) : wikiInfo.pageTitle != null) return false;
        if (parentRevId != null ? !parentRevId.equals(wikiInfo.parentRevId) : wikiInfo.parentRevId != null)
            return false;
        if (revId != null ? !revId.equals(wikiInfo.revId) : wikiInfo.revId != null) return false;
        if (summary != null ? !summary.equals(wikiInfo.summary) : wikiInfo.summary != null) return false;
        if (url != null ? !url.equals(wikiInfo.url) : wikiInfo.url != null) return false;
        return user != null ? user.equals(wikiInfo.user) : wikiInfo.user == null;
    }

    @Override
    public int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + changeSize;
        result = 31 * result + (flags != null ? flags.hashCode() : 0);
        result = 31 * result + (geoIp != null ? geoIp.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(hashtags);
        result = 31 * result + (anon ? 1 : 0);
        result = 31 * result + (bot ? 1 : 0);
        result = 31 * result + (minor ? 1 : 0);
        result = 31 * result + (isNew ? 1 : 0);
        result = 31 * result + (unparolled ? 1 : 0);
        result = 31 * result + Arrays.hashCode(mentions);
        result = 31 * result + (ns != null ? ns.hashCode() : 0);
        result = 31 * result + (pageTitle != null ? pageTitle.hashCode() : 0);
        result = 31 * result + (parentRevId != null ? parentRevId.hashCode() : 0);
        result = 31 * result + (revId != null ? revId.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WikiInfo{" +
                "action='" + action + '\'' +
                ", changeSize=" + changeSize +
                ", flags='" + flags + '\'' +
                ", geoIp=" + geoIp +
                ", hashtags=" + Arrays.toString(hashtags) +
                ", anon=" + anon +
                ", bot=" + bot +
                ", minor=" + minor +
                ", isNew=" + isNew +
                ", unparolled=" + unparolled +
                ", mentions=" + Arrays.toString(mentions) +
                ", ns='" + ns + '\'' +
                ", pageTitle='" + pageTitle + '\'' +
                ", parentRevId='" + parentRevId + '\'' +
                ", revId='" + revId + '\'' +
                ", summary='" + summary + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                '}';
    }

    public static WikiInfo parseIt(String json) throws IOException {
        WikiInfo wi = null;

        ObjectMapper mapper = new ObjectMapper();

        wi = mapper.readValue(json, WikiInfo.class);

        return wi;
    }
}
