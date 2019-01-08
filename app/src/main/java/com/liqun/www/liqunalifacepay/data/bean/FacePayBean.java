package com.liqun.www.liqunalifacepay.data.bean;

import java.io.Serializable;
import java.util.List;

public class FacePayBean {
    public static class FacePayInitBean{

        /**
         * json : {"zimId":"1e11fab27dbe2741fe1e66890bbc5f20c","type":"zimInit","zimInitClientData":"CGQSABohMWUxMWZhYjI3ZGJlMjc0MWZlMWU2Njg5MGJiYzVmMjBjIvoSQ3BVTUNpRmtPR00zTkdWak16ZzJNRGxtWmpjek9URTJZVFpqWlRBMk1UYzBNV0kyWW1NUVpDcnRDM3NpWTI5c2JDSTZleUowYjNCVVpYaDBYMjV2Wm1GalpTSTZJdWF5b2VhY2llYWpnT2ExaS1XSXNPaUV1Q0lzSW5SdmNGUmxlSFJmYVc1MFpXZHlhWFI1SWpvaTVvcUs2SVM0NTZlNzVZV2w1WnlJNVlhRklpd2lkRzl3VkdWNGRGOWhibWRzWlNJNkl1aXZ0LW1kb3VXUWtlV3hqLVc1bGVTNGl1YVd1ZWFSaE9XRGotV2t0Q0lzSW1GamRHbHZiazF2WkdVaU9sc2lOeUlzSWpjaUxDSTNJaXdpTnlJc0lqY2lMQ0kzSWl3aU55SXNJamNpTENJM0lpd2lOeUpkTENKaWIzUjBiMjFVWlhoMElqb2k2Sy0zNW9xSzZJUzQ1cFMtNVlXbDVxR0c1WWFGNUwtZDVveUI1TGlONVlxb0lpd2lkRzl3VkdWNGRGOXRZWGhmY21WamRIZHBaSFJvSWpvaTU2YTc2TC1jNUxpQTU0SzVJaXdpZFhCc2IyRmtSR1Z3ZEdoRVlYUmhJanBtWVd4elpTd2lkWEJzYjJGa1RXOXVhWFJ2Y2xCcFl5STZNU3dpZEc5d1ZHVjRkRjl6ZEdGNUlqb2k1cTJqNVp5bzVhU0U1NUNHSWl3aWRHOXdWR1Y0ZEY5c2FXZG9kQ0k2SXVpRXVPbURxT1M2cnVTNGdPZUN1U0lzSW14cFoyaDBJam94Tnpnc0luUnBiV1VpT2pJd0xDSjBiM0JVWlhoMFgzSmxZM1IzYVdSMGFDSTZJdW1kb09pX2tlUzRnT2VDdVNJc0luSmxkSEo1SWpvekxDSjBiM0JVWlhoMFgzRjFZV3hwZEhraU9pTG9yN2ZwbmFMbGtKSGxzWV9sdVpYa3VJcm1scm5ta1lUbGc0X2xwTFFpTENKMGIzQlVaWGgwWDJKc2RYSWlPaUxsaG8zbXVJWG1tYkRrdUlEbmdya2lMQ0owYjNCVVpYaDBYMkpzYVc1cklqb2k1NXlvNTV5bzU1eThJbjBzSW5WcElqb2lPVGt5SWl3aWRYQnNiMkZrSWpwN0ltMXZaR1VpT2lJeUxqQWlMQ0prWlhOcGNtVmtWMmxrZEdnaU9qRXlPREFzSW5Wd2JHOWhaRjlqYjIxd2NtVnpjMTl5WVhSbElqb3dMamNzSW1OdmJHeGxZM1JwYjI0aU9sc2lSR1Z3ZEdnaUxDSlFZVzV2SWl3aVUweEpVaUpkTENKMWNHeHZZV1JKYldGblpWUjVjR1VpT2lKcWNHVm5JaXdpYkc5blgyTnNZWE56YVdacFpYSWlPaUl4SXpJaWZTd2libTlEWVcxUVpYSnRhWE56YVc5dVYyOXlaSE1pT2lMbHZJRHBnSnJsa0k3bWlZM2xqNl9rdTZYa3ZiX25sS2psaUxmb2hMamxpcF9vZzczdnZJem92NXZsaGFYbGhZM2xyNGJtbDdia3U2TWlMQ0psYm5ZaU9qQXNJbVpoWTJWVWFYQnpJanA3SW14cGJXbDBRV3hsY25RaU9uc2ljbVYwZFhKdVEyOWtaU0k2TWpBNUxDSnlhV2RvZEVKMWRIUnZibFJsZUhRaU9pTG1pSkhubjZYcGdaUGt1b1lpTENKMGFYUnNaU0k2SXVhY3F1aUR2ZWl2aHVXSXEtUzZ1dWlFdUNKOWZTd2lZV3huYjNKcGRHaHRJanA3SW5CdmMyVmZaR2x6ZEdGdVkyVk5ZWGdpT2pFMU1EQXNJbVY1WlVoM2NtRjBhVzhpT2pBdU1UWXNJbk4wWVdOclgzUnBiV1VpT2pFdU1Dd2lkR2h5WlhOb2IyeGtJanA3SWtKaGRFeHBkbVZ1WlhOeklqcGJNQzQxWFgwc0luQnZjMlZmY0dsMFkyZ2lPakF1TVRjc0lubDFibkZwVVhWaGJHbDBlU0k2TkN3aWNHOXpaVjk1WVhjaU9qQXVNaXdpYkdsMlpXNWxjM05mWTI5dFltbHVZWFJwYjI1eklqb2lRbUYwVEdsMlpXNWxjM01pTENKd2IzTmxYMmRoZFhOemFXRnVJam94TGpBc0luQnZjMlZmYlc5MGFXOXVJam94TGpBc0luQnBkR05vVjJWcFoyaDBJam8wTENKd2IzTmxYM0JwZEdOb1RXbHVJam90TUM0eE55d2ljRzl6WlY5eVpXTjBkMmxrZEdnaU9qQXVNeXdpY0c5elpWOXBiblJsWjNKcGRIa2lPakF1T1RVc0ltUnBjMWRsYVdkb2RDSTZOQ3dpZVdGM1YyVnBaMmgwSWpveExDSndiM05sWDJ4cFoyaDBJam93TGpBc0luQnZjMlZmZVdGM1RXbHVJam90TUM0eUxDSmlZWFJNYVhabGJtVnpjMVJvY21WemFHOXNaQ0k2TUM0MUxDSnNiMmRmYkdWMlpXd2lPakFzSW5CdmMyVmZaR2x6ZEdGdVkyVk5hVzRpT2pJd01Dd2laR2xtWm1WeUlqb3dMakUxTENKc2FYWmxibVZ6YzE5amIyMWlhVzVoZEdsdmJpSTZXeUpDWVhSTWFYWmxibVZ6Y3lKZExDSnRZWGhmYVc5a0lqb3hMakFzSW5GMVlXeHBkSGxmYldsdVgzRjFZV3hwZEhraU9qSXdMQ0prWlhCMGFGOWpZV05vWlY5dWRXMGlPak1zSW5OMFlXTnJYM05wZW1VaU9qRXNJbTFwYmw5cGIyUWlPakF1TUgxOUVvQUNnRDN1MWlKZlgzM1hhNnA2blJ3NWEzaWpPYXJ3OVVyTmtnUmtESjZGYVNfSUhQTW0xb2tGdWplZ295ZWFQZEYyajczV0tpNE5FM3dqb3Z6N2U1dzh5MFZJdUdXMEw4NHYxN0c2X2RZM2dkQmZTVWlpbFBMVE1rc2Y1dkMzTWVhWmF2QWVCYzhqbFRvRUlacmZRNUd6XzlVSl84VEJFbmxrOU14VnhHYlVYRGpwWHliUEtNS3U3bmU2bVJPQlg5dHRibWNGREh3NVYyRXNsdTFZTXhxX243MWlUTTFFMmZkMFhBZmhuNnAxTTNLbnhxZXZFY2JwcFpWd3JHbk9kQV9iRUQ0TzFZLURITHhwSHNUT3lKd05kN0prTS1OTTNpaGRJWnZ0ZGFmNE40YnlocU1xdGRaTi1Xd2UwbWxwVnE5NDNrSHpFdW9YTmhMeG8xaVlvV0xxWncqowMKoAMKDmZhY2VwYXlJbmZvTWFwEo0DeyJhY3Rpb25JbmRleCI6IjQiLCJoZWFydEJlYXRQcm90b2NvbCI6IntcImNvbnRlbnRcIjpcIntcXFwiZGVzaXJlZFdpZmlTdGF0dXNcXFwiOntcXFwiY2hhbm5lbFxcXCI6XFxcIjEsMyw2XFxcIixcXFwicGVyaW9kRGV0YWlsXFxcIjpcXFwiNjAwMDBcXFwiLFxcXCJwZXJpb2RNYWNzXFxcIjpcXFwiMTAwMDBcXFwiLFxcXCJyc3NpTWluXFxcIjpcXFwiLTgwXFxcIn0sXFxcImRlc2lyZWRNYWNoaW5lU3RhdHVzXFxcIjp7XFxcImludGVydmFsXFxcIjpcXFwiNjAwMDBcXFwifX1cIixcInJldENvZGVcIjpcIk9LX1NVQ0NFU1NcIixcInRpbWVzdGFtcFwiOjB9IiwidGFyZ2V0QWN0aW9uIjoibW9iaWxlLTQiLCJhY3Rpb25Nb2RlIjoiZHluYW1pYyIsInByb2R1Y3RQYWNrYWdlIjoic21pbGVwYXkifTIFWjUxMTA6DuaIkOWKnyAoWjUxMTAp"}
         * method : ZolozInit
         * stat : 1
         * datetime : 2019/1/5 11:37:48
         */

        private JsonBean json;
        private String method;
        private int stat;
        private String datetime;

        public JsonBean getJson() {
            return json;
        }

        public void setJson(JsonBean json) {
            this.json = json;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public int getStat() {
            return stat;
        }

        public void setStat(int stat) {
            this.stat = stat;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public static class JsonBean {
            /**
             * zimId : 1e11fab27dbe2741fe1e66890bbc5f20c
             * type : zimInit
             * zimInitClientData : CGQSABohMWUxMWZhYjI3ZGJlMjc0MWZlMWU2Njg5MGJiYzVmMjBjIvoSQ3BVTUNpRmtPR00zTkdWak16ZzJNRGxtWmpjek9URTJZVFpqWlRBMk1UYzBNV0kyWW1NUVpDcnRDM3NpWTI5c2JDSTZleUowYjNCVVpYaDBYMjV2Wm1GalpTSTZJdWF5b2VhY2llYWpnT2ExaS1XSXNPaUV1Q0lzSW5SdmNGUmxlSFJmYVc1MFpXZHlhWFI1SWpvaTVvcUs2SVM0NTZlNzVZV2w1WnlJNVlhRklpd2lkRzl3VkdWNGRGOWhibWRzWlNJNkl1aXZ0LW1kb3VXUWtlV3hqLVc1bGVTNGl1YVd1ZWFSaE9XRGotV2t0Q0lzSW1GamRHbHZiazF2WkdVaU9sc2lOeUlzSWpjaUxDSTNJaXdpTnlJc0lqY2lMQ0kzSWl3aU55SXNJamNpTENJM0lpd2lOeUpkTENKaWIzUjBiMjFVWlhoMElqb2k2Sy0zNW9xSzZJUzQ1cFMtNVlXbDVxR0c1WWFGNUwtZDVveUI1TGlONVlxb0lpd2lkRzl3VkdWNGRGOXRZWGhmY21WamRIZHBaSFJvSWpvaTU2YTc2TC1jNUxpQTU0SzVJaXdpZFhCc2IyRmtSR1Z3ZEdoRVlYUmhJanBtWVd4elpTd2lkWEJzYjJGa1RXOXVhWFJ2Y2xCcFl5STZNU3dpZEc5d1ZHVjRkRjl6ZEdGNUlqb2k1cTJqNVp5bzVhU0U1NUNHSWl3aWRHOXdWR1Y0ZEY5c2FXZG9kQ0k2SXVpRXVPbURxT1M2cnVTNGdPZUN1U0lzSW14cFoyaDBJam94Tnpnc0luUnBiV1VpT2pJd0xDSjBiM0JVWlhoMFgzSmxZM1IzYVdSMGFDSTZJdW1kb09pX2tlUzRnT2VDdVNJc0luSmxkSEo1SWpvekxDSjBiM0JVWlhoMFgzRjFZV3hwZEhraU9pTG9yN2ZwbmFMbGtKSGxzWV9sdVpYa3VJcm1scm5ta1lUbGc0X2xwTFFpTENKMGIzQlVaWGgwWDJKc2RYSWlPaUxsaG8zbXVJWG1tYkRrdUlEbmdya2lMQ0owYjNCVVpYaDBYMkpzYVc1cklqb2k1NXlvNTV5bzU1eThJbjBzSW5WcElqb2lPVGt5SWl3aWRYQnNiMkZrSWpwN0ltMXZaR1VpT2lJeUxqQWlMQ0prWlhOcGNtVmtWMmxrZEdnaU9qRXlPREFzSW5Wd2JHOWhaRjlqYjIxd2NtVnpjMTl5WVhSbElqb3dMamNzSW1OdmJHeGxZM1JwYjI0aU9sc2lSR1Z3ZEdnaUxDSlFZVzV2SWl3aVUweEpVaUpkTENKMWNHeHZZV1JKYldGblpWUjVjR1VpT2lKcWNHVm5JaXdpYkc5blgyTnNZWE56YVdacFpYSWlPaUl4SXpJaWZTd2libTlEWVcxUVpYSnRhWE56YVc5dVYyOXlaSE1pT2lMbHZJRHBnSnJsa0k3bWlZM2xqNl9rdTZYa3ZiX25sS2psaUxmb2hMamxpcF9vZzczdnZJem92NXZsaGFYbGhZM2xyNGJtbDdia3U2TWlMQ0psYm5ZaU9qQXNJbVpoWTJWVWFYQnpJanA3SW14cGJXbDBRV3hsY25RaU9uc2ljbVYwZFhKdVEyOWtaU0k2TWpBNUxDSnlhV2RvZEVKMWRIUnZibFJsZUhRaU9pTG1pSkhubjZYcGdaUGt1b1lpTENKMGFYUnNaU0k2SXVhY3F1aUR2ZWl2aHVXSXEtUzZ1dWlFdUNKOWZTd2lZV3huYjNKcGRHaHRJanA3SW5CdmMyVmZaR2x6ZEdGdVkyVk5ZWGdpT2pFMU1EQXNJbVY1WlVoM2NtRjBhVzhpT2pBdU1UWXNJbk4wWVdOclgzUnBiV1VpT2pFdU1Dd2lkR2h5WlhOb2IyeGtJanA3SWtKaGRFeHBkbVZ1WlhOeklqcGJNQzQxWFgwc0luQnZjMlZmY0dsMFkyZ2lPakF1TVRjc0lubDFibkZwVVhWaGJHbDBlU0k2TkN3aWNHOXpaVjk1WVhjaU9qQXVNaXdpYkdsMlpXNWxjM05mWTI5dFltbHVZWFJwYjI1eklqb2lRbUYwVEdsMlpXNWxjM01pTENKd2IzTmxYMmRoZFhOemFXRnVJam94TGpBc0luQnZjMlZmYlc5MGFXOXVJam94TGpBc0luQnBkR05vVjJWcFoyaDBJam8wTENKd2IzTmxYM0JwZEdOb1RXbHVJam90TUM0eE55d2ljRzl6WlY5eVpXTjBkMmxrZEdnaU9qQXVNeXdpY0c5elpWOXBiblJsWjNKcGRIa2lPakF1T1RVc0ltUnBjMWRsYVdkb2RDSTZOQ3dpZVdGM1YyVnBaMmgwSWpveExDSndiM05sWDJ4cFoyaDBJam93TGpBc0luQnZjMlZmZVdGM1RXbHVJam90TUM0eUxDSmlZWFJNYVhabGJtVnpjMVJvY21WemFHOXNaQ0k2TUM0MUxDSnNiMmRmYkdWMlpXd2lPakFzSW5CdmMyVmZaR2x6ZEdGdVkyVk5hVzRpT2pJd01Dd2laR2xtWm1WeUlqb3dMakUxTENKc2FYWmxibVZ6YzE5amIyMWlhVzVoZEdsdmJpSTZXeUpDWVhSTWFYWmxibVZ6Y3lKZExDSnRZWGhmYVc5a0lqb3hMakFzSW5GMVlXeHBkSGxmYldsdVgzRjFZV3hwZEhraU9qSXdMQ0prWlhCMGFGOWpZV05vWlY5dWRXMGlPak1zSW5OMFlXTnJYM05wZW1VaU9qRXNJbTFwYmw5cGIyUWlPakF1TUgxOUVvQUNnRDN1MWlKZlgzM1hhNnA2blJ3NWEzaWpPYXJ3OVVyTmtnUmtESjZGYVNfSUhQTW0xb2tGdWplZ295ZWFQZEYyajczV0tpNE5FM3dqb3Z6N2U1dzh5MFZJdUdXMEw4NHYxN0c2X2RZM2dkQmZTVWlpbFBMVE1rc2Y1dkMzTWVhWmF2QWVCYzhqbFRvRUlacmZRNUd6XzlVSl84VEJFbmxrOU14VnhHYlVYRGpwWHliUEtNS3U3bmU2bVJPQlg5dHRibWNGREh3NVYyRXNsdTFZTXhxX243MWlUTTFFMmZkMFhBZmhuNnAxTTNLbnhxZXZFY2JwcFpWd3JHbk9kQV9iRUQ0TzFZLURITHhwSHNUT3lKd05kN0prTS1OTTNpaGRJWnZ0ZGFmNE40YnlocU1xdGRaTi1Xd2UwbWxwVnE5NDNrSHpFdW9YTmhMeG8xaVlvV0xxWncqowMKoAMKDmZhY2VwYXlJbmZvTWFwEo0DeyJhY3Rpb25JbmRleCI6IjQiLCJoZWFydEJlYXRQcm90b2NvbCI6IntcImNvbnRlbnRcIjpcIntcXFwiZGVzaXJlZFdpZmlTdGF0dXNcXFwiOntcXFwiY2hhbm5lbFxcXCI6XFxcIjEsMyw2XFxcIixcXFwicGVyaW9kRGV0YWlsXFxcIjpcXFwiNjAwMDBcXFwiLFxcXCJwZXJpb2RNYWNzXFxcIjpcXFwiMTAwMDBcXFwiLFxcXCJyc3NpTWluXFxcIjpcXFwiLTgwXFxcIn0sXFxcImRlc2lyZWRNYWNoaW5lU3RhdHVzXFxcIjp7XFxcImludGVydmFsXFxcIjpcXFwiNjAwMDBcXFwifX1cIixcInJldENvZGVcIjpcIk9LX1NVQ0NFU1NcIixcInRpbWVzdGFtcFwiOjB9IiwidGFyZ2V0QWN0aW9uIjoibW9iaWxlLTQiLCJhY3Rpb25Nb2RlIjoiZHluYW1pYyIsInByb2R1Y3RQYWNrYWdlIjoic21pbGVwYXkifTIFWjUxMTA6DuaIkOWKnyAoWjUxMTAp
             */

            private String zimId;
            private String type;
            private String zimInitClientData;

            public String getZimId() {
                return zimId;
            }

            public void setZimId(String zimId) {
                this.zimId = zimId;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getZimInitClientData() {
                return zimInitClientData;
            }

            public void setZimInitClientData(String zimInitClientData) {
                this.zimInitClientData = zimInitClientData;
            }
        }
    }
    public static class FacePayRequestBean{
        private String auth_code; // 用户付款码(刷脸支付传入ftoken)
        private String body; //	订单描述(利群集团刷脸支付)
        private String sys_service_provider_id; // 系统商编号(和pid一致2088031960490332)
        private String operator_id; // 	操作员id
        private String out_trade_no; //	订单号
        private String partnerId; // 商户pid
        private String product_code; //	支付方式(必须传ALIPAY_F2F_PAYMENT)
        private String scene; // 支付场景(必须传security_code)
        private String seller_id; // 各门店商户号
        private String store_id; //	门店号
        private String subject; // 利群集团刷脸付消费
        private String terminal_id; // 款台号
        private String terminal_params; // 商户传入终端设备相关信息
        private String timeout_express; // 轮询时间(默认值1m)
        private float total_amount; //	金额

        public FacePayRequestBean() {
        }

        public FacePayRequestBean(String auth_code, String body, String sys_service_provider_id, String operator_id, String out_trade_no, String partnerId, String product_code, String scene, String seller_id, String store_id, String subject, String terminal_id, String terminal_params, String timeout_express, float total_amount) {
            this.auth_code = auth_code;
            this.body = body;
            this.sys_service_provider_id = sys_service_provider_id;
            this.operator_id = operator_id;
            this.out_trade_no = out_trade_no;
            this.partnerId = partnerId;
            this.product_code = product_code;
            this.scene = scene;
            this.seller_id = seller_id;
            this.store_id = store_id;
            this.subject = subject;
            this.terminal_id = terminal_id;
            this.terminal_params = terminal_params;
            this.timeout_express = timeout_express;
            this.total_amount = total_amount;
        }

        public String getAuth_code() {
            return auth_code;
        }

        public void setAuth_code(String auth_code) {
            this.auth_code = auth_code;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getSys_service_provider_id() {
            return sys_service_provider_id;
        }

        public void setSys_service_provider_id(String sys_service_provider_id) {
            this.sys_service_provider_id = sys_service_provider_id;
        }

        public String getOperator_id() {
            return operator_id;
        }

        public void setOperator_id(String operator_id) {
            this.operator_id = operator_id;
        }

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }

        public String getProduct_code() {
            return product_code;
        }

        public void setProduct_code(String product_code) {
            this.product_code = product_code;
        }

        public String getScene() {
            return scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }

        public String getSeller_id() {
            return seller_id;
        }

        public void setSeller_id(String seller_id) {
            this.seller_id = seller_id;
        }

        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getTerminal_id() {
            return terminal_id;
        }

        public void setTerminal_id(String terminal_id) {
            this.terminal_id = terminal_id;
        }

        public String getTerminal_params() {
            return terminal_params;
        }

        public void setTerminal_params(String terminal_params) {
            this.terminal_params = terminal_params;
        }

        public String getTimeout_express() {
            return timeout_express;
        }

        public void setTimeout_express(String timeout_express) {
            this.timeout_express = timeout_express;
        }

        public float getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(float total_amount) {
            this.total_amount = total_amount;
        }

        @Override
        public String toString() {
            return "FacePayRequestBean{" +
                    "auth_code='" + auth_code + '\'' +
                    ", body='" + body + '\'' +
                    ", sys_service_provider_id='" + sys_service_provider_id + '\'' +
                    ", operator_id='" + operator_id + '\'' +
                    ", out_trade_no='" + out_trade_no + '\'' +
                    ", partnerId='" + partnerId + '\'' +
                    ", product_code='" + product_code + '\'' +
                    ", scene='" + scene + '\'' +
                    ", seller_id='" + seller_id + '\'' +
                    ", store_id='" + store_id + '\'' +
                    ", subject='" + subject + '\'' +
                    ", terminal_id='" + terminal_id + '\'' +
                    ", terminal_params='" + terminal_params + '\'' +
                    ", timeout_express='" + timeout_express + '\'' +
                    ", total_amount=" + total_amount +
                    '}';
        }
    }
    public static class FacePayResponseBean
    implements Serializable {

        /**
         * json : {"alipay_trade_pay_response":{"code":"10000","msg":"Success","buyer_logon_id":"184****0561","buyer_pay_amount":"0.20","buyer_user_id":"2088912022897334","fund_bill_list":[{"amount":"0.20","fund_channel":"ALIPAYACCOUNT"}],"gmt_payment":"2019-01-08 10:33:35","invoice_amount":"0.20","out_trade_no":"lqbh8jy3900082620190108103401","point_amount":"0.00","receipt_amount":"0.20","store_name":"利群超市(诺德广场店)","total_amount":"0.20","trade_no":"2019010822001497330511417331"},"sign":"GkEoIAFvDZOvNVvFQCOKBFYvFZlrwBlmM9J5UzG1MqYIkRYVlc3JFfnlvMlO35LmDIHzmCs9vylmDDswYc3oZByQE0mVzrza1/TtDiItgjRiQB8xV6M5UybLh8lIoBJkHqqfgPQZi/YNEeeXCVPGHOtK0rfMBv22wgknBGIklGiHNabD5HMP51/4jnK4DMP/m0gknqrc5LmD/KHCdWvvYUcAJkKuQXGDjpqR2YpEH+bN4TdtxWCyev0p8BAFiGutV90Zz7LeWDNX7ViNKIP9P9d0NIzFfLc3Shf3242nN3H87PcSTARikDyxMeJliuWJO1vR9S/I6Yy/zYdNC+M4+w=="}
         * method : RePay
         * stat : 1
         * datetime : 2019/1/8 10:33:48
         */

        private JsonBean json;
        private String method;
        private int stat;
        private String datetime;

        public JsonBean getJson() {
            return json;
        }

        public void setJson(JsonBean json) {
            this.json = json;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public int getStat() {
            return stat;
        }

        public void setStat(int stat) {
            this.stat = stat;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public static class JsonBean
        implements Serializable{
            /**
             * alipay_trade_pay_response : {"code":"10000","msg":"Success","buyer_logon_id":"184****0561","buyer_pay_amount":"0.20","buyer_user_id":"2088912022897334","fund_bill_list":[{"amount":"0.20","fund_channel":"ALIPAYACCOUNT"}],"gmt_payment":"2019-01-08 10:33:35","invoice_amount":"0.20","out_trade_no":"lqbh8jy3900082620190108103401","point_amount":"0.00","receipt_amount":"0.20","store_name":"利群超市(诺德广场店)","total_amount":"0.20","trade_no":"2019010822001497330511417331"}
             * sign : GkEoIAFvDZOvNVvFQCOKBFYvFZlrwBlmM9J5UzG1MqYIkRYVlc3JFfnlvMlO35LmDIHzmCs9vylmDDswYc3oZByQE0mVzrza1/TtDiItgjRiQB8xV6M5UybLh8lIoBJkHqqfgPQZi/YNEeeXCVPGHOtK0rfMBv22wgknBGIklGiHNabD5HMP51/4jnK4DMP/m0gknqrc5LmD/KHCdWvvYUcAJkKuQXGDjpqR2YpEH+bN4TdtxWCyev0p8BAFiGutV90Zz7LeWDNX7ViNKIP9P9d0NIzFfLc3Shf3242nN3H87PcSTARikDyxMeJliuWJO1vR9S/I6Yy/zYdNC+M4+w==
             */

            private AlipayTradePayResponseBean alipay_trade_pay_response;
            private String sign;

            public AlipayTradePayResponseBean getAlipay_trade_pay_response() {
                return alipay_trade_pay_response;
            }

            public void setAlipay_trade_pay_response(AlipayTradePayResponseBean alipay_trade_pay_response) {
                this.alipay_trade_pay_response = alipay_trade_pay_response;
            }

            public String getSign() {
                return sign;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public static class AlipayTradePayResponseBean
            implements Serializable{
                /**
                 * code : 10000
                 * msg : Success
                 * buyer_logon_id : 184****0561
                 * buyer_pay_amount : 0.20
                 * buyer_user_id : 2088912022897334
                 * fund_bill_list : [{"amount":"0.20","fund_channel":"ALIPAYACCOUNT"}]
                 * gmt_payment : 2019-01-08 10:33:35
                 * invoice_amount : 0.20
                 * out_trade_no : lqbh8jy3900082620190108103401
                 * point_amount : 0.00
                 * receipt_amount : 0.20
                 * store_name : 利群超市(诺德广场店)
                 * total_amount : 0.20
                 * trade_no : 2019010822001497330511417331
                 */

                private String code;
                private String msg;
                private String buyer_logon_id;
                private String buyer_pay_amount;
                private String buyer_user_id;
                private String gmt_payment;
                private String invoice_amount;
                private String out_trade_no;
                private String point_amount;
                private String receipt_amount;
                private String store_name;
                private String total_amount;
                private String trade_no;
                private List<FundBillListBean> fund_bill_list;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getMsg() {
                    return msg;
                }

                public void setMsg(String msg) {
                    this.msg = msg;
                }

                public String getBuyer_logon_id() {
                    return buyer_logon_id;
                }

                public void setBuyer_logon_id(String buyer_logon_id) {
                    this.buyer_logon_id = buyer_logon_id;
                }

                public String getBuyer_pay_amount() {
                    return buyer_pay_amount;
                }

                public void setBuyer_pay_amount(String buyer_pay_amount) {
                    this.buyer_pay_amount = buyer_pay_amount;
                }

                public String getBuyer_user_id() {
                    return buyer_user_id;
                }

                public void setBuyer_user_id(String buyer_user_id) {
                    this.buyer_user_id = buyer_user_id;
                }

                public String getGmt_payment() {
                    return gmt_payment;
                }

                public void setGmt_payment(String gmt_payment) {
                    this.gmt_payment = gmt_payment;
                }

                public String getInvoice_amount() {
                    return invoice_amount;
                }

                public void setInvoice_amount(String invoice_amount) {
                    this.invoice_amount = invoice_amount;
                }

                public String getOut_trade_no() {
                    return out_trade_no;
                }

                public void setOut_trade_no(String out_trade_no) {
                    this.out_trade_no = out_trade_no;
                }

                public String getPoint_amount() {
                    return point_amount;
                }

                public void setPoint_amount(String point_amount) {
                    this.point_amount = point_amount;
                }

                public String getReceipt_amount() {
                    return receipt_amount;
                }

                public void setReceipt_amount(String receipt_amount) {
                    this.receipt_amount = receipt_amount;
                }

                public String getStore_name() {
                    return store_name;
                }

                public void setStore_name(String store_name) {
                    this.store_name = store_name;
                }

                public String getTotal_amount() {
                    return total_amount;
                }

                public void setTotal_amount(String total_amount) {
                    this.total_amount = total_amount;
                }

                public String getTrade_no() {
                    return trade_no;
                }

                public void setTrade_no(String trade_no) {
                    this.trade_no = trade_no;
                }

                public List<FundBillListBean> getFund_bill_list() {
                    return fund_bill_list;
                }

                public void setFund_bill_list(List<FundBillListBean> fund_bill_list) {
                    this.fund_bill_list = fund_bill_list;
                }

                public static class FundBillListBean
                implements Serializable{
                    /**
                     * amount : 0.20
                     * fund_channel : ALIPAYACCOUNT
                     */

                    private String amount;
                    private String fund_channel;

                    public String getAmount() {
                        return amount;
                    }

                    public void setAmount(String amount) {
                        this.amount = amount;
                    }

                    public String getFund_channel() {
                        return fund_channel;
                    }

                    public void setFund_channel(String fund_channel) {
                        this.fund_channel = fund_channel;
                    }
                }
            }
        }
    }
}
