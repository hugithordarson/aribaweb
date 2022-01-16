/*
    Copyright 1996-2008 Ariba, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    $Id: //ariba/platform/ui/aribaweb/ariba/ui/aribaweb/core/AWPageRedirect.java#10 $
*/

package ariba.ui.aribaweb.core;

import ariba.ui.aribaweb.util.AWContentType;
import ariba.ui.aribaweb.util.AWEncodedString;

public final class AWPageRedirect extends AWComponent
{
    public static final String PageName = "AWPageRedirect";
    AWComponent _page;

    public void setPage (AWComponent page)
    {
        _page = page;
    }

    public void applyValues(AWRequestContext requestContext, AWComponent component)
    {
        return;
    }

    public AWResponseGenerating invokeAction(AWRequestContext requestContext, AWComponent component)
    {
        return _page;
    }

    public void renderResponse(AWRequestContext requestContext, AWComponent component)
    {
        AWEncodedString senderId = requestContext.nextElementId();
        String pageUrl = AWComponentActionRequestHandler.SharedInstance.urlWithSenderId(requestContext, senderId);
        AWResponse response = requestContext.response();
        response.setHeaderForKey(pageUrl, "location");
        response.setContentType(AWContentType.TextHtml);
        response.setStatus(AWResponse.StatusCodes.RedirectFound);
        requestContext.forceFullPageRefresh();
    }

}
