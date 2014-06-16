/*
 * flibit2D Game Engine
 * © 2011 Ethan "flibitijibibo" Lee
 * http://www.flibitijibibo.com/
 * 
 * FilterProcessing
 * Static methods for processing audio effects.
 */

package twelveengine.audio;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;

public class FilterProcessing {
	
	/** Creates a reversed version of the PCM data.
	 * @param sourceData The 16-bit stereo PCM data
	 * @return The reversed PCM data
	 */
	public static ByteBuffer createReverseData(ByteBuffer sourceData) {
		ByteBuffer reverseBuffer = BufferUtils.createByteBuffer(sourceData.limit());
		for (int x = sourceData.limit() - 2; x > 1; x -= 4) {
			reverseBuffer.putShort(sourceData.getShort(x - 2));
			reverseBuffer.putShort(sourceData.getShort(x));
		}
		reverseBuffer.rewind();
		return reverseBuffer;
	}
	
	/** Creates a wahwah'd version of the PCM data.
	 * @param sourceData The 16-bit stereo PCM data
	 * @param sampleRate The sample rate of the PCM data
	 * @return The wahwah'd PCM data
	 */
	public static ByteBuffer createWahData(ByteBuffer sourceData, int sampleRate) {
		ByteBuffer result = BufferUtils.createByteBuffer(sourceData.limit());
		
		// Create the separate channels
		ByteBuffer leftChannelBytes = BufferUtils.createByteBuffer(sourceData.limit() / 2);
		ByteBuffer rightChannelBytes = BufferUtils.createByteBuffer(sourceData.limit() / 2);
		for (int x = 0; x < sourceData.limit() - 2; x += 4) {
			leftChannelBytes.putShort(sourceData.getShort(x));
			rightChannelBytes.putShort(sourceData.getShort(x + 2));
		}
		
		applyWahFilter(leftChannelBytes, sampleRate);
		applyWahFilter(rightChannelBytes, sampleRate);
		
		// Create the final ByteBuffer
		for (int x = 0; x < sourceData.limit() / 2; x += 2) {
			result.putShort(leftChannelBytes.getShort(x));
			result.putShort(rightChannelBytes.getShort(x));
		}
		result.rewind();
		return result;
	}
	
	/** Creates a distorted version of the PCM data.
	 * @param sourceData The 16-bit stereo PCM data
	 * @param sampleRate The sample rate of the PCM data
	 * @return The distorted PCM data
	 */
	public static ByteBuffer createDistortionData(ByteBuffer sourceData, int sampleRate) {
		ByteBuffer result = BufferUtils.createByteBuffer(sourceData.limit());
		
		// Create the separate channels
		ByteBuffer leftChannelBytes = BufferUtils.createByteBuffer(sourceData.limit() / 2);
		ByteBuffer rightChannelBytes = BufferUtils.createByteBuffer(sourceData.limit() / 2);
		for (int x = 0; x < sourceData.limit() - 2; x += 4) {
			leftChannelBytes.putShort(sourceData.getShort(x));
			rightChannelBytes.putShort(sourceData.getShort(x + 2));
		}
		
		applyDistortion(leftChannelBytes, sampleRate);
		applyDistortion(rightChannelBytes, sampleRate);
		
		// Create the final ByteBuffer
		for (int x = 0; x < sourceData.limit() / 2; x += 2) {
			result.putShort(leftChannelBytes.getShort(x));
			result.putShort(rightChannelBytes.getShort(x));
		}
		result.rewind();
		return result;
	}
	
	/** Applies a wah filter to a mono PCM track. Adaption of Audacity's Wahwah.cpp.
	 * @param channel The PCM track
	 * @param sampleRate The sample rate of the PCM track
	 */
	private static void applyWahFilter(ByteBuffer channel, int sampleRate) {
		// #define lfoskipsamples = 30
		int lfoskipsamples = 30;
		// EffectWahwah::EffectWahwah()
		float freq = 1.5f;
		float depth = 0.7f;
		float res = 2.5f;
		float freqofs = 0.3f;
		// EffectWahwah::NewTrackSimpleMono()
		double lfoskip = (freq * 2 * Math.PI / (double) sampleRate);
		int skipcount = 0;
		double xn1 = 0, xn2 = 0, yn1 = 0, yn2 = 0;
		double b0 = 0.0, b1 = 0.0, b2 = 0.0;
		double a0 = 0.0, a1 = 0.0, a2 = 0.0;
		// EffectWahwah::ProcessSimpleMono()
		double frequency, omega, sn, cs, alpha;
		double in, out;
		
		// We are currently just messing with the left channel.
		for (int x = 0; x < channel.limit(); x += 2) {
			in = channel.getShort(x);
			
			if ((skipcount++) % lfoskipsamples == 0) {
				frequency = (1 + Math.cos(skipcount * lfoskip)) / 2;
				frequency = frequency * depth * (1 - freqofs) + freqofs;
				frequency = Math.exp((frequency - 1) * 6);
				omega = Math.PI * frequency;
				sn = Math.sin(omega);
				cs = Math.cos(omega);
				alpha = sn / (2 * res);
				b0 = (1 - cs) / 2;
				b1 = 1 - cs;
				b2 = (1 - cs) / 2;
				a0 = 1 + alpha;
				a1 = -2 * cs;
				a2 = 1 - alpha;
			}
			out = (b0 * in + b1 * xn1 + b2 * xn2 - a1 * yn1 - a2 * yn2) / a0;
			xn2 = xn1;
			xn1 = in;
			yn2 = yn1;
			yn1 = out;
			
			// Prevents clipping
			if (out < -32768.0)
				out = -32768.0;
			else if (out > 32767.0)
				out = 32767.0;
			
			channel.putShort(x, (short) (out));
		}
	}
	
	/** Overdrives then softens a mono PCM track.
	 * @param channel The PCM track
	 * @param sampleRate The sample rate of the PCM track
	 */
	private static void applyDistortion(ByteBuffer channel, int sampleRate) {
		double in, out;
		for (int x = 0; x < channel.limit(); x += 2) {
			in = channel.getShort(x);
			
			out = in * 50;
			
			// "Prevents" clipping
			if (out < -32768.0)
				out = -32768.0;
			else if (out > 32767.0)
				out = 32767.0;
			
			out /= 10;
			
			channel.putShort(x, (short) out);
		}
	}
}
