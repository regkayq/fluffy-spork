import tkinter as tk
from tkinter import ttk, filedialog, messagebox
import subprocess
import threading
import os

FFMPEG_PATH = r"Q:\ffmpeg\bin\ffmpeg.exe"


class FFmpegCombinerApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("FFmpeg Video + Audio Combiner")
        self.resizable(False, False)
        self._build_ui()

    def _build_ui(self):
        pad = {"padx": 10, "pady": 5}

        # ── Video file ──────────────────────────────────────────────────────
        tk.Label(self, text="Video file:", anchor="w").grid(
            row=0, column=0, sticky="w", **pad
        )
        self.video_var = tk.StringVar()
        tk.Entry(self, textvariable=self.video_var, width=55).grid(
            row=0, column=1, **pad
        )
        tk.Button(self, text="Browse…", command=self._pick_video).grid(
            row=0, column=2, **pad
        )

        # ── Audio file ──────────────────────────────────────────────────────
        tk.Label(self, text="Audio file:", anchor="w").grid(
            row=1, column=0, sticky="w", **pad
        )
        self.audio_var = tk.StringVar()
        tk.Entry(self, textvariable=self.audio_var, width=55).grid(
            row=1, column=1, **pad
        )
        tk.Button(self, text="Browse…", command=self._pick_audio).grid(
            row=1, column=2, **pad
        )

        # ── Output file ─────────────────────────────────────────────────────
        tk.Label(self, text="Output file:", anchor="w").grid(
            row=2, column=0, sticky="w", **pad
        )
        self.output_var = tk.StringVar()
        tk.Entry(self, textvariable=self.output_var, width=55).grid(
            row=2, column=1, **pad
        )
        tk.Button(self, text="Browse…", command=self._pick_output).grid(
            row=2, column=2, **pad
        )

        # ── Combine button ───────────────────────────────────────────────────
        self.combine_btn = tk.Button(
            self,
            text="Combine",
            command=self._start_combine,
            bg="#4CAF50",
            fg="white",
            font=("", 10, "bold"),
            padx=20,
        )
        self.combine_btn.grid(row=3, column=0, columnspan=3, pady=10)

        # ── Progress bar ─────────────────────────────────────────────────────
        self.progress = ttk.Progressbar(self, mode="indeterminate", length=400)
        self.progress.grid(row=4, column=0, columnspan=3, padx=10, pady=(0, 5))

        # ── Log output ───────────────────────────────────────────────────────
        self.log = tk.Text(self, height=10, width=72, state="disabled", bg="#1e1e1e", fg="#d4d4d4")
        self.log.grid(row=5, column=0, columnspan=3, padx=10, pady=(0, 10))

        scrollbar = tk.Scrollbar(self, command=self.log.yview)
        scrollbar.grid(row=5, column=3, sticky="ns", pady=(0, 10))
        self.log["yscrollcommand"] = scrollbar.set

    # ── File pickers ─────────────────────────────────────────────────────────

    def _pick_video(self):
        path = filedialog.askopenfilename(
            title="Select video file",
            filetypes=[("Video / media files", "*.mp4 *.mkv *.webm *.ts *.avi *.mov *.flv *.m4v"), ("All files", "*.*")],
        )
        if path:
            self.video_var.set(path)
            self._suggest_output()

    def _pick_audio(self):
        path = filedialog.askopenfilename(
            title="Select audio file",
            filetypes=[("Audio / media files", "*.m4a *.aac *.mp3 *.opus *.ogg *.wav *.webm *.ts"), ("All files", "*.*")],
        )
        if path:
            self.audio_var.set(path)
            self._suggest_output()

    def _pick_output(self):
        path = filedialog.asksaveasfilename(
            title="Save combined file as",
            defaultextension=".mp4",
            filetypes=[("MP4 video", "*.mp4"), ("MKV video", "*.mkv"), ("All files", "*.*")],
        )
        if path:
            self.output_var.set(path)

    def _suggest_output(self):
        """Pre-fill output path when both inputs are set."""
        v = self.video_var.get()
        if v and not self.output_var.get():
            base, _ = os.path.splitext(v)
            self.output_var.set(base + "_combined.mp4")

    # ── Combine logic ─────────────────────────────────────────────────────────

    def _start_combine(self):
        video = self.video_var.get().strip()
        audio = self.audio_var.get().strip()
        output = self.output_var.get().strip()

        if not video:
            messagebox.showerror("Missing input", "Please select a video file.")
            return
        if not audio:
            messagebox.showerror("Missing input", "Please select an audio file.")
            return
        if not output:
            messagebox.showerror("Missing output", "Please choose an output destination.")
            return

        self.combine_btn.config(state="disabled")
        self.progress.start(10)
        self._log_clear()
        self._log(f"Video  : {video}\n")
        self._log(f"Audio  : {audio}\n")
        self._log(f"Output : {output}\n")
        self._log("Running FFmpeg…\n\n")

        thread = threading.Thread(
            target=self._run_ffmpeg,
            args=(video, audio, output),
            daemon=True,
        )
        thread.start()

    def _run_ffmpeg(self, video, audio, output):
        cmd = [
            FFMPEG_PATH,
            "-y",             # overwrite without asking
            "-i", video,
            "-i", audio,
            "-c:v", "copy",   # copy video stream – no re-encode
            "-c:a", "aac",    # encode audio to AAC
            "-b:a", "192k",
            "-map", "0:v:0",
            "-map", "1:a:0",
            output,
        ]

        try:
            proc = subprocess.Popen(
                cmd,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
                encoding="utf-8",
                errors="replace",
            )
            for line in proc.stdout:
                self._log(line)
            proc.wait()
            success = proc.returncode == 0
        except FileNotFoundError:
            self._log(f"\nERROR: FFmpeg not found at:\n  {FFMPEG_PATH}\n")
            success = False
        except Exception as exc:
            self._log(f"\nERROR: {exc}\n")
            success = False

        self.after(0, self._on_done, success, output)

    def _on_done(self, success, output):
        self.progress.stop()
        self.combine_btn.config(state="normal")
        if success:
            self._log("\n✓ Done! File saved to:\n  " + output + "\n")
            messagebox.showinfo("Done", f"Combined file saved to:\n{output}")
        else:
            self._log("\n✗ FFmpeg exited with an error. See log above.\n")
            messagebox.showerror("Error", "FFmpeg failed. Check the log for details.")

    # ── Log helpers ───────────────────────────────────────────────────────────

    def _log(self, text):
        self.log.config(state="normal")
        self.log.insert("end", text)
        self.log.see("end")
        self.log.config(state="disabled")

    def _log_clear(self):
        self.log.config(state="normal")
        self.log.delete("1.0", "end")
        self.log.config(state="disabled")


if __name__ == "__main__":
    app = FFmpegCombinerApp()
    app.mainloop()
