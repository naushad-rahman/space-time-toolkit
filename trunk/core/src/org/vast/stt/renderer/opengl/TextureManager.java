/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.renderer.opengl;

import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.vast.ows.sld.Symbolizer;
import org.vast.stt.project.DataStyler;
import org.vast.stt.style.RasterPixelGraphic;
import org.vast.stt.style.RasterTileGraphic;
import org.vast.stt.style.TextureStyler;
import org.vast.util.MessageSystem;


/**
 * <p><b>Title:</b><br/>
 * Texture Manager
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Generated POT or NPOT textures according to OpenGL hardware 
 * capabilities. POT textures can be generated by resampling or
 * padding with 100% transparent white pixels, in which case
 * texture coordinates are automatically adjusted.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 13, 2006
 * @version 1.0
 */
public class TextureManager
{
    protected static Hashtable<Symbolizer, GLTextureTable> symTextureTables
               = new Hashtable<Symbolizer, GLTextureTable>();
    protected GL gl;
    protected GLU glu;
    protected boolean npotSupported;
    protected boolean normalizationRequired;
    
    
    class GLTexture
    {
        protected int id = -1;
        protected boolean needsUpdate = true;
        protected int widthPadding;
        protected int heightPadding;
    }
    
    
    class GLTextureTable extends Hashtable<Object, GLTexture>
    {
        private final static long serialVersionUID = 0;
    }
    
    
    public TextureManager(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
        
        // find out which texture 2D target to use
        String glExtensions = gl.glGetString(GL.GL_EXTENSIONS);
        if (glu.gluCheckExtension("GL_ARB_texture_rectangle", glExtensions) ||
            glu.gluCheckExtension("GL_EXT_texture_rectangle", glExtensions))
        {
            OpenGLCaps.TEXTURE_2D_TARGET = GL.GL_TEXTURE_RECTANGLE_EXT;
            MessageSystem.display("--> NPOT textures supported <--", false);
            npotSupported = true;
            normalizationRequired = false;
        }
        else
        {
            OpenGLCaps.TEXTURE_2D_TARGET = GL.GL_TEXTURE_2D;
            MessageSystem.display("--> NPOT textures NOT supported <--", false);
            MessageSystem.display("--> Textures will be padded with transparent pixels or resampled <--", false);
            npotSupported = false;
            normalizationRequired = true;
        }
        
        // enable right texture target
        gl.glEnable(OpenGLCaps.TEXTURE_2D_TARGET);
    }
    
    
    /**
     * Retrieves stored textureID or create a new one along
     * with the corresponding texture in OpenGL memory.
     * @param tex
     * @return
     */
    public void useTexture(TextureStyler styler, RasterTileGraphic tex)
    {
        Symbolizer sym = styler.getSymbolizer();
        
        synchronized (symTextureTables)
        {
            // try to find table for this symbolizer
            GLTextureTable textureTable = symTextureTables.get(sym);
            
            // create if it doesn't exist
            if (textureTable == null)
            {
                textureTable = new GLTextureTable();
                symTextureTables.put(sym, textureTable);
            }
                
            // try to find texture for this tile
            GLTexture texInfo = textureTable.get(tex.block);
            
            // create if it doesn't exist
            if (texInfo == null)
            {
                texInfo = new GLTexture();
                textureTable.put(tex.block, texInfo);
            }            
            
            // create new texture if it needs update
            if (texInfo.needsUpdate)
            {
                texInfo.needsUpdate = false;
                createTexture(styler, tex, texInfo);
            }
            
            // otherwise just bind existing one
            else
            {
                if (texInfo.id > 0)
                {
                    gl.glBindTexture(OpenGLCaps.TEXTURE_2D_TARGET, texInfo.id);
                    //System.err.println("Tex #" + texInfo.id + " used");
                }
            }
            
            // transfer padding info to RasterTileGraphic
            tex.heightPadding = texInfo.heightPadding;
            tex.widthPadding = texInfo.widthPadding;
        }
    }
    
    
    /**
     * Creates a new texture by transfering data from styler to GL memory
     * @param styler
     * @param tex
     * @param texInfo
     */
    protected void createTexture(TextureStyler styler, RasterTileGraphic tex, GLTexture texInfo)
    {
        // fetch texture data from styler
        fillRGBAData(styler, tex, texInfo);
        
        // if texture was successfully constructed, bind it with GL
        if (tex.hasRasterData)
        {
            // create new texture name and bind it
            int[] id = new int[1];
            gl.glGenTextures(1, id, 0); 
            gl.glBindTexture(OpenGLCaps.TEXTURE_2D_TARGET, id[0]);
            
            // set texture parameters
            gl.glTexParameteri(OpenGLCaps.TEXTURE_2D_TARGET, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(OpenGLCaps.TEXTURE_2D_TARGET, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            
            // create texture in GL memory
            gl.glTexImage2D(OpenGLCaps.TEXTURE_2D_TARGET, 0, GL.GL_RGBA,
                            tex.width + texInfo.widthPadding, tex.height + texInfo.heightPadding,
                            0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, tex.rasterData);
            
            // erase temp buffer
            tex.rasterData = null;
            
            // set new id and reset needsUpdate flag
            int oldID = texInfo.id;
            texInfo.id = id[0];
            
            // delete previous texture if needed
            if (oldID > 0)
            {
                gl.glDeleteTextures(1, new int[] {oldID}, 0);
                //System.err.println("Tex #" + oldID + " deleted");
            }
                        
            //System.err.println("Tex #" + texInfo.id + " created");
        }
    }
    
    
    /**
     * Clears all display lists used by this symbolizer
     * @param sym
     */
    public void clearTextures(DataStyler styler)
    {
        synchronized (symTextureTables)
        {
            Symbolizer sym = styler.getSymbolizer();
            GLTextureTable textureTable = symTextureTables.get(sym);
            
            if (textureTable != null)
            {
                Enumeration<GLTexture> textureEnum = textureTable.elements();
                while (textureEnum.hasMoreElements())
                {
                    GLTexture texInfo = textureEnum.nextElement();
                    if (texInfo.id > 0)
                    {
                        gl.glDeleteTextures(1, new int[] {texInfo.id}, 0);
                        //System.err.println("Tex #" + texInfo.id + " deleted");
                    }
                }
                
                symTextureTables.remove(sym);
            }
        }
    }
    
    
    /**
     * Clears texture associated with this symbolizer/object pair
     * @param sym
     * @param obj
     */
    public void clearTexture(Symbolizer sym, Object obj)
    {
        GLTextureTable textureTable = symTextureTables.get(sym);
        
        if (textureTable != null)
        {
            GLTexture texInfo = textureTable.get(obj);
            
            if (texInfo != null && texInfo.id > 0)
            {
                gl.glDeleteTextures(1, new int[] {texInfo.id}, 0);
                //System.err.println("Tex #" + texInfo.id + " deleted");
            }
            
            textureTable.remove(obj);
        }
    }
    
    
    /**
     * Create an RGBA texture based on data passed by styler
     * @param styler
     * @param tex
     * @param texInfo
     */
    protected void fillRGBAData(TextureStyler styler, RasterTileGraphic tex, GLTexture texInfo)
    {
        int paddedWidth = tex.width;
        int paddedHeight = tex.height;
        int trueWidth = tex.width;
        int trueHeight = tex.height;
        boolean padded = false;
        
        
        // handle case of padding
        if (!npotSupported)
        {
            // determine closest power of 2
            double powerWidth = Math.log(paddedWidth)/Math.log(2);
            double powerHeight = Math.log(paddedHeight)/Math.log(2);

            // compute new width only if not already power of 2
            if (Math.floor(powerWidth) != powerWidth)
            {
                paddedWidth = (int) Math.pow(2, (int) powerWidth + 1);
                padded = true;
            }
            
            // compute new height only if not already power of 2
            if (Math.floor(powerHeight) != powerHeight)
            {
                paddedHeight = (int) Math.pow(2, (int) powerHeight + 1);
                padded = true;
            }
            
            // display warning message if padding is needed
            if (padded)
            {
                System.err.println("Texture will be padded to have a power of two size:");
                System.err.println("   original size: " + trueWidth + " x " + trueHeight);
                System.err.println("     padded size: " + paddedWidth + " x " + paddedHeight);
                
                texInfo.widthPadding = paddedWidth - trueWidth;
                texInfo.heightPadding = paddedHeight - trueHeight;
            }
        }
        
        // create byte buffer of the right size
        byte[] buffer = new byte[paddedWidth*paddedHeight*4];
        int index = 0;
        
        for (int j=0; j<trueHeight; j++)
        {
            for (int i=0; i<trueWidth; i++)
            {
                RasterPixelGraphic pixel = styler.getPixel(i, j);
                buffer[index] = (byte)pixel.r;
                index++;
                buffer[index] = (byte)pixel.g;
                index++;
                buffer[index] = (byte)pixel.b;
                index++;
                buffer[index] = -1;//(byte)pixel.a;
                index++;
            }
            
            // skip padding bytes
            index += texInfo.widthPadding*4;
        }
        
        tex.rasterData = ByteBuffer.wrap(buffer);
        tex.hasRasterData = true;
        tex.hasColorMapData = false;
    }
    
    
    /**
     * Create an RGB texture based on data passed by styler
     * @param tex
     */
    protected void fillRGBData(DataStyler styler, RasterTileGraphic texture)
    {
        
    }


    public boolean isNpotSupported()
    {
        return npotSupported;
    }


    public boolean isNormalizationRequired()
    {
        return normalizationRequired;
    }
}