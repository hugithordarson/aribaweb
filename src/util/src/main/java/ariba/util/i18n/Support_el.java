/*
    Copyright 2013 Ariba, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    $Id: //ariba/platform/util/core/ariba/util/i18n/Support_el.java#1 $
*/

package ariba.util.i18n;


/**
    default i18n for Greek

    @aribaapi private
*/

public class Support_el extends I18NSupport
{
    /*-----------------------------------------------------------------------
        Public Methods
      -----------------------------------------------------------------------*/

    /**
       Class constructor.
    */
    public Support_el ()
    {
    }

    /**
        Returns the file encoding string for the specified locale

        @param locale the locale to get the encoding string of
        @return the encoding string
        @aribaapi documented
    */
    public String fileEncoding ()
    {
        return I18NConstants.IANACharset[I18NConstants.Cp1253];
    }

    /**
        Returns the MIME header encoding string

        @return the encoding string
        @aribaapi private
    */
    public String mimeHeaderEncoding ()
    {
        return I18NConstants.EncodingB;
    }

    /**
        Returns the MIME body encoding string

        @return the encoding string
        @aribaapi private
    */
    public String mimeBodyEncoding ()
    {
        return I18NConstants.CharacterEncoding[I18NConstants.Cp1253];
    }

    /**
        Returns the MIME body charset string

        @return the encoding string
        @aribaapi private
    */
    public String mimeBodyCharset ()
    {
        return I18NConstants.IANACharset[I18NConstants.Cp1253];
    }
}
