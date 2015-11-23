/**
 * ********************************************************************************************************
 * <p/>
 * All rights reserved © 2015  -  Innovative Imaging Technologies  -  www.iitreacts.com
 * This computer program may not be used, copied, distributed, corrected, modified, translated,
 * transmitted or assigned without Innovative Imaging Technologies's prior written authorization.
 * <p/>
 * Created by  :  pteyssedre
 * Date        :  15-10-17
 * Application :  Paranoya .
 * Package     :  ca.teyssedre.crypto .
 * <p/>
 * ********************************************************************************************************
 */
package ca.teyssedre.crypto;

public interface ICryptoCallback<T> {

    void OnComplete(T data);
}
