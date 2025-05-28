import { HTTP_INTERCEPTORS, HttpEvent, HttpHandler, HttpInterceptor, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { Injectable, Provider } from '@angular/core';
import { Observable, catchError, from, switchMap } from 'rxjs';
import {StorageService} from "../../services/storage.service";

@Injectable({
	providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {

	constructor(
		private storage: StorageService,
	) { }

	base64Decode(str: string) {
		const percentEncodedStr = atob(str).split('').map(function (c) {
			return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
		}).join('');
		return decodeURIComponent(percentEncodedStr);
	}

	intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		// if (request.body?.token) {
		// 	return next.handle(request);
		// }

		return from(Promise.all([
			this.storage.get('_token'),
		])).pipe(
			switchMap(async ([token]) => {
				if (!token) {
					return next.handle(request);
				}

				const headers = request.headers.set('Authorization', `Bearer ${token}`);

				const requestClone = request.clone({
					headers
				});

				return next.handle(requestClone);
			}),
			switchMap((handle) => handle),
			catchError(async (res: any) => {
				console.error(res);
				throw res;
			})
		);
	}
}

export const AuthInterceptorProvider: Provider = {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
};
