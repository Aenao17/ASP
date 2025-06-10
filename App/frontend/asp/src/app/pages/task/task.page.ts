import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TaskService} from "../../services/task.service";
import {AuthService} from "../../services/auth.service";
interface Task {
  title: string;
  description: string;
  status: string;
  ownerId: string;
  createdAt: string;
  deadline: string;
}
@Component({
  selector: 'app-task',
  templateUrl: './task.page.html',
  styleUrls: ['./task.page.scss'],
  standalone: false
})
export class TaskPage implements OnInit {

  tasks: Task[] = [];
  taskForm: FormGroup;
  loading = false;
  error: string | null = null;
  userRole: string | null = null;

  constructor(
    private taskService: TaskService,
    private fb: FormBuilder,
    private auth: AuthService
  ) {
    this.taskForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      status: ['pending', Validators.required],
      deadline: ['', Validators.required],
      volunteers: [''],
      subTasks: ['']
    });// Assuming getUserRole returns the current user's role
  }

  ngOnInit() {
    this.loadTasks();
    this.getUserRole();
  }

  async getUserRole(): Promise<void> {
    try {
      this.userRole = await this.auth.getUserRole();
    }catch (err:any){
      if(err.status==200){
        this.userRole = err.error.text;
      }
    }
  }

  async loadTasks() {
    this.loading = true;
    this.error = null;
    try {
      const data: any = await this.taskService.getTasks();
      console.log('Tasks loaded:', data);
      this.tasks = data;
    } catch (err: any) {
      if(err.status === 401) {
      this.error = 'Failed to load tasks.';
      console.error(err);
        }
    } finally {
      this.loading = false;
    }
  }

  async onSubmit() {
    if (this.taskForm.invalid) return;

    const raw = this.taskForm.value;
    const taskData = {
      ...raw,
      volunteers: raw.volunteers ? raw.volunteers.split(',').map((v: string) => v.trim()) : [],
      subTasks: raw.subTasks ? raw.subTasks.split(',').map((s: string) => parseInt(s.trim(), 10)) : []
    };

    this.loading = true;
    this.error = null;
    taskData.createdAt = new Date().toISOString(); // Set createdAt to current time
    taskData.ownerId = "1"; // Assuming getUsername returns the current user's ID
    taskData.status = taskData.status.toUpperCase();
    try {
      await this.taskService.createTask(taskData);
      this.taskForm.reset({status: 'pending'});
      this.loadTasks();
    } catch (err: any) {
      if(err.status === 401) {
      this.error = 'Failed to create task.';
      console.error(err);
        }
    } finally {
      this.loading = false;
      this.loadTasks();
    }
  }
}
